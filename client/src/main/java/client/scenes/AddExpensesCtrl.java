package client.scenes;

import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddExpensesCtrl {

    private ServerUtils server;
    private MainCtrl mainCtrl;

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

    /**
     * Initializes the controller.
     */
    public void initialize() {
        String[] participantNames = {"Martijn", "Horia", "Iulia", "Amanda", "Mihnea", "Fayaz"};

        // Add text fields dynamically for each participant
        for (int i = 0; i < participantNames.length; i++) {
            Label participantLabel = new Label(participantNames[i]);
            participantLabel.setPrefWidth(80);
            TextField amountTextField = new TextField();
            amountTextField.setPromptText("Amount");
            amountTextField.setPrefWidth(70); // Set a fixed width for all text fields

            HBox hbox = new HBox(participantLabel, amountTextField);
            hbox.setSpacing(10.0); // Adjust spacing between label and text field
            hbox.setLayoutY(30 + i * 40); // Set a fixed Y position for each participant

            participantsVBox.getChildren().add(hbox);
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
