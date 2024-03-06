package client.scenes;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class AddExpensesCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;
    private List<CheckBox> participantCheckboxes;

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
     */
    public void initialize() {
        String[] participantNames = {"Martijn", "Horia", "Iulia", "Amanda", "Mihnea", "Fayaz"};

        participantCheckboxes = new ArrayList<>();

        for (String name : participantNames) {
            CheckBox participantCheckbox = new CheckBox(name);
            participantCheckboxes.add(participantCheckbox);

            // Set preferred width and style for the checkbox
            participantCheckbox.setPrefWidth(80);
            participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");

            // Add the checkbox to the VBox
            participantsVBox.getChildren().add(participantCheckbox);
        }

        // Initialize currency options and set default selection
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
        // Handle adding expense here
        // To be implemented
        String amount = amountTextField.getText();
        System.out.println("Amount: " + amount);
    }

    /**
     * Handles the action when the user navigates back to the overview screen.
     */
    public void back() {
        mainCtrl.goToOverview();
    }
}
