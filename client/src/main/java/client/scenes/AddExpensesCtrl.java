package client.scenes;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddExpensesCtrl implements Initializable {

    private ServerUtils server;
    private MainCtrl mainCtrl;
    private List<CheckBox> participantCheckboxes;
    @FXML
    private TextField nameTextField;

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
     * Initializes the controller.
     * From Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (mainCtrl.getSelectedEvent() != null) {
            Long eventId = mainCtrl.getSelectedEvent().getId();
            List<String> participants = server.getParticipantNicknamesByEventId(eventId);


            // Clear existing checkboxes
            participantsVBox.getChildren().clear();

            // Populate checkboxes with participant names
            for (String participant : participants) {
                CheckBox participantCheckbox = new CheckBox(participant);
                participantCheckboxes.add(participantCheckbox);

                participantCheckbox.setPrefWidth(80);
                participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");

                participantsVBox.getChildren().add(participantCheckbox);
            }
        } else {
            showErrorDialog("No event found");
        }

        ObservableList<String> currencyList = FXCollections.observableArrayList(
                "USD", "EUR", "GBP", "JPY");
        currencyComboBox.setItems(currencyList);
        currencyComboBox.getSelectionModel().select("EUR");
    }

    @FXML
    private void handleEquallyCheckbox() {
        boolean selected = equallyCheckbox.isSelected();
        for (CheckBox checkbox : participantCheckboxes) {
            checkbox.setSelected(selected);
        }
    }


    /**
     * Handles the action when the user adds an expense.
     */
    @FXML
    private void addExpense() {
        Event selectedEvent = new Event();
        String payerName = nameTextField.getText();
        Participant payer = server.getParticipantByNickname(selectedEvent.getId(), payerName);

        // Step 2: Retrieve the amount and currency from the UI components
        String amountText = amountTextField.getText();
        double amount = Double.parseDouble(amountText);

        // Step 3: Create an expense object
        Expense expense = new Expense();
        expense.setAmount(amount);
        // Set other properties of the Expense object as needed

        // Step 4: Set the payer of the expense
        expense.setPayer(payer);

        List<Participant> owners = new ArrayList<>();
        // Step 5: Add the expense to the selected event for each selected participant
        for (CheckBox checkbox : participantCheckboxes) {
            if (checkbox.isSelected()) {
                String participantName = checkbox.getText();
                Participant participant = server.getParticipantByNickname(
                        selectedEvent.getId(), participantName);
                // Add the participant as an owner of the expense
                owners.add(participant);
            }
        }
        expense.setOwers(owners);

        // Step 6: Add the expense to the event
        // Assuming you have a method to get the selected event
        selectedEvent.addExpense(expense);

        // Step 7: Save the expense to the server
        // Assuming you have a method to save the expense to the server
        server.addExpenseToEvent(selectedEvent.getId(), expense);
    }

    /**
     * Handles the action when the user navigates back to the overview screen.
     */
    public void back() {
        mainCtrl.goToOverview();
    }

    /**
     * Shows error message
     * @param errorMessage message to be shown
     */
    private void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
