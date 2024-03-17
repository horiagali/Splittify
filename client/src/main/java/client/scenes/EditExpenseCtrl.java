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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditExpenseCtrl implements Initializable {

    private Event event;
    private Expense expense;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    List<CheckBox> checkBoxes;
    @FXML
    private TextField amountTextField;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private Button delete;

    @FXML
    private Button edit;

    @FXML
    private CheckBox equallyCheckbox;

    @FXML
    private TextField nameTextField;

    @FXML
    private VBox participantsVBox;

    @FXML
    private TextField purposeTextField;

    @Inject
    public EditExpenseCtrl(Expense expense, Event event, ServerUtils server, MainCtrl mainCtrl) {
        this.expense = expense;
        this.event = event;
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @FXML
    void addExpense(ActionEvent event) {

    }

    @FXML
    void back(ActionEvent event) {
    }

    @FXML
    void handleEquallyCheckbox(ActionEvent event) {

    }

    /**
     * Initializes the controller.
     * From Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Participant> participantNames = this.event.getParticipants();
        checkBoxes = new ArrayList<>();

        this.nameTextField.setText(expense.getPayer().getNickname());
        this.purposeTextField.setText(expense.getTitle());
        this.amountTextField.setText(Double.toString(expense.getAmount()));
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

}
