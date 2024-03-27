package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddExpensesCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private List<CheckBox> participantCheckboxes = new ArrayList<>();
    private List<Participant> selectedParticipants = new ArrayList<>();
    private List<Participant> allParticipants = new ArrayList<>();
    @FXML
    private TextField purposeTextField;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox participantsVBox;
    @FXML
    private TextField amountTextField;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    private CheckBox equallyCheckbox;
    @FXML
    private ComboBox<Participant> payerComboBox;
    @FXML
    private ComboBox<Tag> tagComboBox;

    /**
     * Constructs an instance of AddExpensesCtrl.
     *
     * @param server    The ServerUtils instance.
     * @param mainCtrl  The MainCtrl instance.
     */
    @Inject
    public AddExpensesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the controller.
     * From Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addKeyboardNavigationHandlers();
        loadTags();
        currencyComboBox.setOnKeyPressed(this::handleCurrencySwitch);

        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            loadParticipants();
        }

        // Populate currency ComboBox
        currencyComboBox.setItems(FXCollections.observableArrayList("USD", "EUR", "GBP", "JPY"));
        currencyComboBox.getSelectionModel().select("EUR");
    }

    /**
     * Loads the participants for the selected event and populates the UI elements accordingly.
     * If no event is selected or no participants are found
     * for the selected event, an error dialog is displayed.
     */
    private void loadParticipants() {
        participantsVBox.getChildren().clear();
        payerComboBox.getItems().clear();
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent == null) {
            showErrorDialog("No event selected.");
            return;
        }
        List<Participant> participants = server.getParticipants(selectedEvent.getId());
        if (participants == null || participants.isEmpty()) {
            showErrorDialog("No participants found for the selected event.");
            return;
        }
        allParticipants.addAll(participants);
        populateParticipantCheckboxes(participants);
        configurePayerComboBox(participants);
    }

    /**
     * Populates the participant checkboxes in the UI with the provided list of participants.
     *
     * @param participants The list of participants to be displayed as checkboxes.
     */
    private void populateParticipantCheckboxes(List<Participant> participants) {
        List<String> participantNicknames = new ArrayList<>();
        for (Participant participant : participants) {
            CheckBox participantCheckbox = createParticipantCheckbox(participant);
            participantsVBox.getChildren().add(participantCheckbox);
            participantCheckboxes.add(participantCheckbox);
            participantNicknames.add(participant.getNickname());
        }
    }

    /**
     * Creates a CheckBox for the given participant.
     *
     * @param participant The participant for whom the CheckBox is created.
     * @return The created CheckBox.
     */
    private CheckBox createParticipantCheckbox(Participant participant) {
        CheckBox participantCheckbox = new CheckBox(participant.getNickname());
        participantCheckbox.setPrefWidth(80);
        participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");
        participantCheckbox.setOnAction(event ->
                handleParticipantCheckboxAction(participantCheckbox));
        return participantCheckbox;
    }

    /**
     * Configures the payer ComboBox with the provided list of participants.
     *
     * @param participants The list of participants to populate the ComboBox.
     */
    private void configurePayerComboBox(List<Participant> participants) {
        payerComboBox.setCellFactory(param -> createParticipantListCell());
        payerComboBox.setButtonCell(createParticipantListCell());
        payerComboBox.setItems(FXCollections.observableArrayList(participants));
    }

    /**
     * Creates a ListCell for the ComboBox to display participant nicknames.
     *
     * @return The created ListCell.
     */
    private ListCell<Participant> createParticipantListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNickname());
                }
            }
        };
    }

    /**
     * Refreshes
     */
    public void refreshParticipants() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            loadParticipants();
            loadTags();
        }
    }

    /**
     * Loads the tags associated with the selected event from the
     * server and populates the tagComboBox.
     * If no event is selected or no tags are found for
     * the selected event, the tagComboBox will remain empty.
     */
    private void loadTags() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            List<Tag> tags = server.getTags(selectedEvent.getId());
            if (tags != null && !tags.isEmpty()) {
                ObservableList<Tag> tagList = FXCollections.observableArrayList(tags);
                tagComboBox.setItems(tagList);
                // Customize the appearance of the ComboBox items to display only tag names
                tagComboBox.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Tag item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                });
                tagComboBox.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Tag item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                });
            } else {
                tagComboBox.getItems().clear();
            }
        } else {
            tagComboBox.getItems().clear();
        }
    }


    /**
     * Handles participant checkboxes
     * @param checkBox checkbox
     */
    @FXML
    private void handleParticipantCheckboxAction(CheckBox checkBox) {
        String participantName = checkBox.getText();
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent == null) {
            return;
        }
        List<Participant> participants = server.getParticipants(selectedEvent.getId());
        Participant selectedParticipant = participants.stream()
                .filter(participant -> participantName.equals(participant.getNickname()))
                .findFirst()
                .orElse(null);
        if (selectedParticipant != null) {
            if (checkBox.isSelected()) {
                selectedParticipants.add(selectedParticipant);
            } else {
                selectedParticipants.remove(selectedParticipant);
            }
        }
    }

    /**
     * Selects all checkboxes if someone presses split equally
     */
    @FXML
    private void handleEquallyCheckbox() {
        if (!participantCheckboxes.isEmpty()) {
            boolean selected = equallyCheckbox.isSelected();
            selectedParticipants.clear();
            if (selected) {
                Event selectedEvent = OverviewCtrl.getSelectedEvent();
                if (selectedEvent != null) {
                    List<Participant> participants = server.getParticipants(selectedEvent.getId());
                    selectedParticipants.addAll(participants);
                }
            }
            for (CheckBox checkbox : participantCheckboxes) {
                checkbox.setSelected(selected);
            }
        } else {
            showErrorDialog("There are no participants to split the cost between");
        }
    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                addExpense();
            }
        });
    }

    /**
     * Handles switching of currencies with just keyboard presses
     * @param event keyboard press
     */
    private void handleCurrencySwitch(KeyEvent event) {
        if (event.getCode() == KeyCode.DOWN) {
            currencyComboBox.getSelectionModel().selectNext();
        } else if (event.getCode() == KeyCode.UP) {
            currencyComboBox.getSelectionModel().selectPrevious();
        }
    }

    /**
     * Handles the action when the user adds an expense.
     */
    @FXML
    private void addExpense() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent == null) {
            showErrorDialog("No event selected.");
            return;
        }

        String title = purposeTextField.getText();
        String amountText = amountTextField.getText();

        //if () return;

        double amount = parseAmount(amountText);
        if (validateAmount(amountText) || amount < 0) return;

        Participant payer = findPayer();
        if (payer == null) return;

        checkSelectedParticipants(selectedParticipants);

        Tag selectedTag = tagComboBox.getValue();
        if (selectedTag == null) {
            showErrorDialog("Please select a tag.");
            return;
        }

        Expense expense = createExpense(title, amount, payer, selectedParticipants, selectedTag);
        if (expense == null) return;

        saveExpense(selectedEvent, expense);
        clearFieldsAndShowOverview(selectedEvent);
    }

    /**
     * Checks if a participant has been selected
     * @param selectedParticipants list of selected participants
     */
    private void checkSelectedParticipants(List<Participant> selectedParticipants) {
        if (selectedParticipants.isEmpty()) {
            showErrorDialog("Please select at least one participant to split the cost.");
            return;
        }
    }

    /**
     * Validates the amount entered by the user.
     *
     * @param amountText The text representing the amount entered by the user.
     * @return {@code true} if the amount is valid, {@code false} otherwise.
     */
    private boolean validateAmount(String amountText) {
        if (amountText.isEmpty()) {
            showErrorDialog("Please enter the amount.");
            return true;
        }
        try {
            Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for the amount.");
            return true;
        }
        return false;
    }

    /**
     * Parses the amount entered by the user.
     *
     * @param amountText The text representing the amount entered by the user.
     * @return The parsed amount as a double value.
     */
    private double parseAmount(String amountText) {
        try {
            return Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for the amount.");
            return -1;
        }
    }

    /**
     * Finds the payer selected by the user.
     *
     * @return The selected payer participant, or {@code null} if not found.
     */
    private Participant findPayer() {
        String payerName = payerComboBox.getValue().getNickname();
        Participant payer = allParticipants.stream()
                .filter(participant -> payerName.equals(participant.getNickname()))
                .findFirst()
                .orElse(null);
        if (payer == null) {
            showErrorDialog("Payer not found.");
        }
        return payer;
    }


    /**
     * Creates an expense object.
     *
     * @param title               The title of the expense.
     * @param amount              The amount of the expense.
     * @param payer               The participant who paid the expense.
     * @param selectedParticipants The participants involved in the expense.
     * @param selectedTag         The tag associated with the expense.
     * @return The created expense object.
     */
    private Expense createExpense(String title, double amount, Participant payer,
                                  List<Participant> selectedParticipants, Tag selectedTag) {
        return new Expense(title, amount, payer, selectedParticipants, selectedTag);
    }

    /**
     * Saves the expense to the server and clears selected participants.
     *
     * @param selectedEvent The selected event.
     * @param expense       The expense to be saved.
     */
    private void saveExpense(Event selectedEvent, Expense expense) {
        System.out.println(expense);
        server.addExpenseToEvent(selectedEvent.getId(), expense);
        selectedParticipants.clear();
    }

    /**
     * Clears input fields and shows the event overview.
     *
     * @param selectedEvent The selected event.
     */
    private void clearFieldsAndShowOverview(Event selectedEvent) {
        refreshUI();
        mainCtrl.showEventOverview(selectedEvent);
    }

    /**
     * Refreshes the UI after adding an expense.
     */
    private void refreshUI() {
        loadParticipants();
        purposeTextField.clear();
        amountTextField.clear();
        equallyCheckbox.setSelected(false);
    }

    /**
     * Navigates back to the overview screen.
     */
    public void back() {
        mainCtrl.goToOverview();
    }

    /**
     * Shows an error dialog with the given error message.
     * @param errorMessage The error message to display.
     */
    private void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
