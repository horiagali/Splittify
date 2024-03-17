package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditExpenseCtrl implements Initializable {

    private Event event;
    private Expense expense;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    private List<CheckBox> checkBoxes;
    private boolean isSplitEqually;

    @FXML
    private TextField amountTextField;
    @FXML
    private Label messageLabel;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    private Button delete;
    @FXML
    private Button cancel;
    @FXML
    private Button editButton;
    @FXML
    private CheckBox equallyCheckbox;
    @FXML
    private ChoiceBox<String> nameChoiceBox;
    @FXML
    private VBox participantsVBox;
    @FXML
    private TextField purposeTextField;

    /**
     * Constructor for EditExpenseCtrl.
     * @param expense the expense in question.
     * @param server the serverUtils.
     * @param mainCtrl main controller.
     */
    @Inject
    public EditExpenseCtrl(Expense expense, ServerUtils server, MainCtrl mainCtrl) {
        this.expense = expense;
        this.event = expense.getEvent();
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Participant> participantNames = this.event.getParticipants();
        checkBoxes = new ArrayList<>();

        this.nameChoiceBox.getItems().addAll(
                        event.getParticipants()
                        .stream()
                        .map(Participant::getNickname)
                        .toList());
        this.nameChoiceBox.getSelectionModel().select
                (this.event.getParticipants().indexOf(this.expense.getPayer()));
        this.purposeTextField.setText(expense.getTitle());
        this.amountTextField.setText(Double.toString(expense.getAmount()));
        this.isSplitEqually = equallyCheckbox.isSelected();
        //TODO: add original currency to editor
        ObservableList<String> currencyList = FXCollections.observableArrayList(
                "USD", "EUR", "GBP", "JPY");
        currencyComboBox.setItems(currencyList);
        currencyComboBox.getSelectionModel().select("EUR");

        for (Participant participant : participantNames) {
            CheckBox participantCheckbox = new CheckBox(participant.getNickname());
            checkBoxes.add(participantCheckbox);

            participantCheckbox.setPrefWidth(80);
            participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");

            participantsVBox.getChildren().add(participantCheckbox);
        }
    }

    /**
     * Edits the expense instance without database modifications.
     * Is called by edit which is set on action.
     */
    public void editExpense() {
        expense.reverseSettleBalance();
        expense.setTitle(purposeTextField.getText());
        expense.setAmount(Double.parseDouble(amountTextField.getText()));
        expense.setPayer(this.event.getParticipants()
                .stream()
                .filter(x -> x.getNickname().equals(nameChoiceBox.getValue()))
                .findFirst()
                .orElseThrow());
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
            editButton.setOnAction(event -> editExpense());
            //TODO: Edit in database backend.
        }
        catch (Exception e) {
            messageLabel.setText("Invalid input, try again!");
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
    public void cancel() {
        mainCtrl.goToOverview();
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
     * @param event the actionEvent of the button.
     */
    @FXML
    public void deleteExpense(ActionEvent event) {
        this.event.getExpenses().remove(this.expense);
        //TODO: Delete from database.
    }
}
