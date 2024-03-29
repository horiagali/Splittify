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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.ResourceBundle;

public class EditExpenseCtrl implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    private static Event event;
    private static Expense expense;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private List<CheckBox> participantCheckboxes = new ArrayList<>();
    private List<Participant> selectedParticipants = new ArrayList<>();
    private boolean isSplitEqually;

    @FXML
    private ComboBox<Participant> nameComboBox;
    @FXML
    private TextField purposeTextField;
    @FXML
    private TextField amountTextField;
    @FXML
    private CheckBox equallyCheckbox;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    private VBox participantsVBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Tag> tagComboBox;


    /**
     * Constructor for EditExpenseCtrl.
     *
     * @param server   the serverUtils.
     * @param mainCtrl main controller.
     */
    @Inject
    public EditExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        event = OverviewCtrl.getSelectedEvent();
        expense = OverviewCtrl.getSelectedExpense();

        if (event != null && expense != null) {
            loadParticipants();
            loadTags();

            currencyComboBox.setItems
                    (FXCollections.observableArrayList("USD", "EUR", "GBP", "JPY"));
            currencyComboBox.getSelectionModel().select("EUR");

            setInitialPage();
        }
        else
            return;
    }

    /**
     * Tags.
     */
    private void loadTags() {
        List<Tag> tags = server.getTags(event.getId());
        tags = tags.stream()
                .filter(tag -> !"gifting money".equalsIgnoreCase(tag.getName()))
                .toList();

        ObservableList<Tag> listTags = FXCollections.observableArrayList(tags);
        tagComboBox.setItems(listTags);
        tagComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                    setStyle("-fx-background-color: " + item.getColor());
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
                    setStyle("-fx-background-color: " + item.getColor());
                }
            }
        });
    }

    /**
     *
     */
    private void loadParticipants() {
        participantsVBox.getChildren().clear();

        for (Participant participant : event.getParticipants()) {
            CheckBox participantCheckbox = new CheckBox(participant.getNickname());
            participantCheckbox.setPrefWidth(80);
            participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");
            participantCheckbox.setOnAction(event ->
                    handleParticipantCheckboxAction(participantCheckbox));

            participantsVBox.getChildren().add(participantCheckbox);
            participantCheckboxes.add(participantCheckbox);
        }
    }

    /**
     * Handles participant checkboxes
     *
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

    private void setInitialPage() {
        nameComboBox.setValue(expense.getPayer());
        datePicker.setValue(LocalDate.parse(expense.getDate().toString()));
        amountTextField.setText(String.valueOf(expense.getAmount()));
        tagComboBox.setValue(expense.getTag());
    }

    /**
     * Getter for event.
     *
     * @return event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Getter for expense.
     *
     * @return expense.
     */
    public Expense getExpense() {
        return expense;
    }

    /**
     * Setter for expense.
     *
     * @param newExpense the new expense.
     */
    public static void setExpense(Expense newExpense) {
        expense = newExpense;
    }

    /**
     * Setter for event.
     *
     * @param newEvent the new event.
     */
    public static void setEvent(Event newEvent) {
        event = newEvent;
    }

    /**
     * Edits the expense instance without database modifications.
     * Is called by edit which is set on action.
     */
    public void editExpense() {
        expense.reverseSettleBalance();
        getTitle();
        getAmount();
        getDateFromPicker();
        getPayer();
        expense.setOwers(selectedParticipants);
        expense.settleBalance();
    }

    /**
     * Edits the expense object, as well as in storage.
     */
    @FXML
    public void edit() {
        try {
            editExpense();
            server.editExpense(expense);
            mainCtrl.showEventOverview(event);
        } catch (Exception e) {
            System.out.println("Error, try again!");
        }
    }

    /**
     * Checkbox for equal split of the amount or not.
     */
    @FXML
    void handleEquallyCheckbox() {
        this.isSplitEqually = flipEquality(isSplitEqually);
        //TODO: logic of balancing out without equality.
    }


    /**
     * Returns to main overview.
     */
    @FXML
    public void back() {
        participantsVBox.getChildren().clear();
        nameComboBox.getItems().clear();
        mainCtrl.showEventOverview(event);
    }

    /**
     * Logic for equal split button.
     *
     * @param value the boolean value to flip.
     * @return the flipped value.
     */
    public boolean flipEquality(boolean value) {
        return !value;
    }

    /**
     * Deletes an expense from an event and from the database.
     */
    public void deleteExpense() {
        event.getExpenses().remove(expense);
        server.deleteExpense(expense);
        mainCtrl.showEventOverview(event);
    }

    /**
     * Gets the payer of the edited event.
     */
    @FXML
    public void getPayer() {
        try {
            Participant nick = nameComboBox.getValue();
            expense.setPayer(nick);
        } catch (Exception e) {
            System.out.println("Input a valid participant!");
        }
    }

    /**
     * Gets the purpose of the event.
     */
    @FXML
    public void getTitle() {
        try {
            expense.setTitle(purposeTextField.getText());
        } catch (Exception e) {
            System.out.println("Enter a valid purpose!");
        }
    }

    /**
     * Gets the amount of the expense.
     */
    @FXML
    public void getAmount() {
        try {
            expense.setAmount(Double.parseDouble(amountTextField.getText()));
        } catch (Exception e) {
            System.out.println("Enter a valid amount!");
        }
    }

    /**
     * Gets the date from the date picker.
     */
    @FXML
    public void getDateFromPicker() {
        try {
            event.setDate((Date) datePicker.getUserData());
        } catch (Exception e) {
            System.out.println("Input a valid date!");
        }
    }

}
