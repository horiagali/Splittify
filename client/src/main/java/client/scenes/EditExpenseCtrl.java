package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

//import java.net.URL;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.ResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditExpenseCtrl implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    private Event event;
    private Expense expense;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private List<CheckBox> checkBoxes;
    private boolean isSplitEqually;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField purposeTextField;
    @FXML
    private TextField amountTextField;
    @FXML
    private CheckBox equallyCheckbox;
    @FXML
    private Button edit;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    private VBox participantsVBox;
    @FXML
    private Button delete;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button cancelButton;





    /**
     * Constructor for EditExpenseCtrl.
     * @param expense the expense in question.
     * @param server the serverUtils.
     * @param mainCtrl main controller.
     */
    @Inject
    public EditExpenseCtrl(Expense expense, ServerUtils server, MainCtrl mainCtrl) {
        this.expense = expense;
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.event = OverviewCtrl.getSelectedEvent();
    }

    /**
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (OverviewCtrl.getSelectedEvent() != null) {
            event = OverviewCtrl.getSelectedEvent();
            List<Participant> participantNames = this.event.getParticipants();
            checkBoxes = new ArrayList<>();

            this.nameTextField.setText(expense.getPayer().getNickname());
            this.purposeTextField.setText(expense.getTitle());
            this.amountTextField.setText(Double.toString(expense.getAmount()));
            this.isSplitEqually = equallyCheckbox.isSelected();
            //TODO: add original currency to editor
            ObservableList<String> currencyList = FXCollections.observableArrayList(
                    "USD", "EUR", "GBP", "JPY");
            currencyComboBox.setItems(currencyList);
            currencyComboBox.getSelectionModel().select("EUR");

            datePicker.setValue(LocalDate.parse(expense.getDate().toString()));

            for (Participant participant : participantNames) {
                CheckBox participantCheckbox = new CheckBox(participant.getNickname());
                checkBoxes.add(participantCheckbox);

                participantCheckbox.setPrefWidth(80);
                participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");

                participantsVBox.getChildren().add(participantCheckbox);
            }
        }
    }

    /**
     * Getter for event.
     * @return event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Getter for expense.
     * @return expense.
     */
    public Expense getExpense() {
        return expense;
    }

    /**
     * Setter for expense.
     * @param expense the new expense.
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * Setter for event.
     * @param event the new event.
     */
    public void setEvent(Event event) {
        this.event = event;
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
        expense.setOwers(participantsVBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(checkBox -> this.event.getParticipants().get(checkBoxes.indexOf(checkBox)))
                .collect(Collectors.toList()));
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
        }
        catch (Exception e) {
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
        mainCtrl.showEventOverview(event);
    }

    /**
     * Logic for equal split button.
     * @param value the boolean value to flip.
     * @return the flipped value.
     */
    public boolean flipEquality(boolean value) {
        return !value;
    }

    /**
     * Deletes an expense from an event and from the database.
     */
    @FXML
    public void deleteExpense() {
        this.event.getExpenses().remove(this.expense);
        server.deleteExpense(expense);
        mainCtrl.showEventOverview(this.event);
    }

    /**
     * Gets the payer of the edited event.
     */
    @FXML
    public void getPayer() {
        try {
            String nick = nameTextField.getText().replace(" ", "");
            expense.setPayer(event.getParticipants().stream()
                    .filter(x -> x.getNickname().equals(nick)).toList().getFirst());
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
        }
        catch (Exception e) {
            System.out.println("Input a valid date!");
        }
    }

}
