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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AddExpensesCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private List<CheckBox> participantCheckboxes;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField purposeTextField;
    @FXML
    private AnchorPane anchorPane;

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
    @FXML
    private DatePicker datePicker;

    /**
     * Initializes the controller.
     * From Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addKeyboardNavigationHandlers();
        currencyComboBox.setOnKeyPressed(this::handleCurrencySwitch);
        if (OverviewCtrl.getSelectedEvent() != null) {
            Long eventId = OverviewCtrl.getSelectedEvent().getId();
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
        }

        ObservableList<String> currencyList = FXCollections.observableArrayList(
                "USD", "EUR", "GBP", "JPY");
        currencyComboBox.setItems(currencyList);
        currencyComboBox.getSelectionModel().select("EUR");
    }

    @FXML
    private void handleEquallyCheckbox() {
        if(participantCheckboxes != null) {
            boolean selected = equallyCheckbox.isSelected();
            for (CheckBox checkbox : participantCheckboxes) {
                checkbox.setSelected(selected);
            }
        }
        else{
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

        if(selectedEvent != null && participantCheckboxes != null) {
            String title = purposeTextField.getText();
            String payerName = nameTextField.getText();
            Participant payer = server.getParticipantByNickname(selectedEvent.getId(), payerName);

            // Step 2: Retrieve the amount and currency from the UI components
            String amountText = amountTextField.getText();
            double amount = Double.parseDouble(amountText);

            // Step 3: Create an expense object
            Expense expense = new Expense();
            expense.setAmount(amount);
            expense.setTitle(title);

            // Step 4: Set the payer of the expense
            expense.setPayer(payer);
            List<Participant> owners = new ArrayList<>();

            // Step 5: Add the expense to the selected event for each selected participant
            for (CheckBox checkbox : participantCheckboxes) {
                if (checkbox.isSelected()) {
                    String participantName = checkbox.getText();
                    Participant participant = server.getParticipantByNickname(
                            selectedEvent.getId(), participantName);
                    owners.add(participant);
                }
            }

            // Step 6: Add the date of the expense
            LocalDate localDate = datePicker.getValue();
            Date date = Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC));
            expense.setDate(date);

            expense.setOwers(owners);
            selectedEvent.addExpense(expense);
            server.addExpenseToEvent(selectedEvent.getId(), expense);
        }
        else{
            showErrorDialog("Can't add an expense, because some values may be null");
        }
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
