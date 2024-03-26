package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
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
    private TextField nameTextField;
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
    private Label selectedCurrencyLabel;

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
     * Loads the participants
     */
    private void loadParticipants() {
        participantsVBox.getChildren().clear();
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            System.out.println("Selected event: " + selectedEvent.getTitle());
            List<Participant> participants = server.getParticipants(selectedEvent.getId());
            allParticipants.addAll(participants);
            if (participants != null && !participants.isEmpty()) {
                for (Participant participant : participants) {
                    CheckBox participantCheckbox = new CheckBox(participant.getNickname());
                    participantCheckbox.setPrefWidth(80);
                    participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");
                    participantCheckbox.setOnAction(event ->
                            handleParticipantCheckboxAction(participantCheckbox));
                    participantsVBox.getChildren().add(participantCheckbox);
                    participantCheckboxes.add(participantCheckbox);
                }
                System.out.println("Participants loaded successfully.");
            } else {
                showErrorDialog("No participants found for the selected event.");
            }
        } else {
            showErrorDialog("No event selected.");
        }
    }

    /**
     * Refreshes
     */
    public void refreshParticipants() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            loadParticipants();
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
            if (selected) {
                selectedParticipants.clear();
                Event selectedEvent = OverviewCtrl.getSelectedEvent();
                if (selectedEvent != null) {
                    List<Participant> participants = server.getParticipants(selectedEvent.getId());
                    selectedParticipants.addAll(participants);
                }
            } else {
                selectedParticipants.clear();
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
        if (selectedEvent != null && !selectedParticipants.isEmpty()) {
            String title = purposeTextField.getText();
            String payerName = nameTextField.getText();

            // Find the payer in the allParticipants list
            Participant payer = allParticipants.stream()
                    .filter(participant -> payerName.equals(participant.getNickname()))
                    .findFirst()
                    .orElse(null);

            if (payer != null) {
                String amountText = amountTextField.getText();
                double amount = Double.parseDouble(amountText);
                Expense expense = new Expense(title, amount, payer, 
                selectedParticipants, new Tag("testing", "#42f572"));
                expense.getTag().setId(1l);
                System.out.println(expense.toString());
                server.addExpenseToEvent(selectedEvent.getId(), expense);
                selectedParticipants.clear();
                refreshUI(); // Refresh UI
            } else {
                showErrorDialog("Payer not found.");
            }
        } else {
            showErrorDialog("Can't add an expense because some values may be null.");
        }
    }

    /**
     * Refreshes the UI after adding an expense.
     */
    private void refreshUI() {
        // Reload participants and clear input fields
        loadParticipants();
        nameTextField.clear();
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
