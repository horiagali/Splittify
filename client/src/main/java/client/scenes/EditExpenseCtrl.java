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

    @Inject
    public EditExpenseCtrl(Expense expense, ServerUtils server, MainCtrl mainCtrl) {
        this.expense = expense;
        this.event = expense.getEvent();
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void editExpense() {
        expense.reverseSettleBalance();
        expense.setTitle(purposeTextField.getText());
        expense.setAmount(Double.parseDouble(amountTextField.getText()));
        expense.setPayer(this.event.getParticipants().stream().filter(x -> x.getNickname().equals(nameChoiceBox.getValue())).findFirst().orElseThrow());
        expense.setOwers(participantsVBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(checkBox -> this.event.getParticipants().get(checkBoxes.indexOf(checkBox)))
                .collect(Collectors.toList()));
        expense.settleBalance();
    }

    @FXML
    public void edit() {
        try {
            editButton.setOnAction(event -> editExpense());
            //TODO: Edit in database.
        }
        catch (Exception e) {
            messageLabel.setText("Please introduce some valid data!");
        }
    }

    @FXML
    void handleEquallyCheckbox(ActionEvent event) {
        this.isSplitEqually = flipEquality(isSplitEqually);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Participant> participantNames = this.event.getParticipants();
        checkBoxes = new ArrayList<>();

        this.nameChoiceBox.getItems().addAll(event.getParticipants().stream().map(Participant::getNickname).toList());
        this.nameChoiceBox.getSelectionModel().select(this.event.getParticipants().indexOf(this.expense.getPayer()));
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

    @FXML
    public void cancel() {
        mainCtrl.goToOverview();
    }

    public boolean flipEquality(boolean value) {
        return !value;
    }

    @FXML
    public void deleteExpense() {
        event.getExpenses().remove(this.expense);
        //TODO: Delete from database.
    }
}
