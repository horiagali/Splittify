package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class EditParticipantCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Participant participant;
    private static Event event;



    @FXML
    private TextField nicknameTextField;
    @FXML
    private Label nicknameLabel;

    @FXML
    private TextField emailTextField;
    @FXML
    private Label emailLabel;

    @FXML
    private TextField balanceTextField;
    @FXML
    private Label balanceLabel;

    @FXML
    private TextField bicTextField;
    @FXML
    private Label bicLabel;

    @FXML
    private Menu languageMenu;
    
    @FXML
    private ToggleGroup currencyGroup;

    @FXML
    private TextField ibanTextField;
    @FXML
    private Label ibanLabel;

    @FXML
    private Label errorMessageLabel;

    /**
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EditParticipantCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     *
     * @return
     * return the participant
     */
    public static Participant getParticipant() {
        return participant;
    }

    /**
     *
     * @param participant
     */
    public static void setParticipant(Participant participant) {
        EditParticipantCtrl.participant = participant;
    }

    /**
     *
     * @return  returns the event
     */
    public static Event getEvent() {
        return event;
    }

    /**
     *
     * @param event
     */
    public static void setEvent(Event event) {
        EditParticipantCtrl.event = event;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayParticipantDetails();
    }

    void displayParticipantDetails() {
        if (participant != null) {
            if (participant.getNickname() != "") {
                nicknameLabel.setText(participant.getNickname());
                nicknameTextField.setText(participant.getNickname());
            } else {
                nicknameLabel.setText("No nickname added");
            }

            if (participant.getEmail() != "") {
                emailTextField.setText(participant.getEmail());
                emailLabel.setText(participant.getEmail());
            } else {
                emailLabel.setText("No email added");
            }

            balanceLabel.setText(String.valueOf(participant.getBalance()));


            if (participant.getBic() != "") {
                bicTextField.setText(participant.getBic());
                bicLabel.setText(participant.getBic());
            } else {
                bicLabel.setText("No BIC added");
            }

            if (participant.getIban() != "") {
                ibanTextField.setText(participant.getIban());
                ibanLabel.setText(participant.getIban());
            } else {
                ibanLabel.setText("No IBAN added");
            }


        }
    }




    @FXML
    private void deleteParticipant() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Participant");
        alert.setHeaderText("Are you sure you want to delete this participant?");
        alert.setContentText("This action cannot be undone. If this participant is a payer" +
        " in an expense, this expense will be removed. It will be removed as ower if it was one.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Delete the participant from the server
                List<Expense> expensesWithParticipantAsPayer = 
                server.getExpensesByEventId(event.getId()).stream()
                .filter(x -> x.getPayer().equals(participant)).toList();
                for(Expense expenseToDelete : expensesWithParticipantAsPayer) {
                    server.deleteExpense(event.getId(), expenseToDelete);
                }
                List<Expense> expensesWithParticipantAsOwer = 
                server.getExpensesByEventId(event.getId()).stream()
                .filter(x -> x.getOwers().contains(participant)).toList();
                for(Expense expenseToUpdate : expensesWithParticipantAsOwer) {
                    expenseToUpdate.getOwers().remove(participant);
                    server.updateExpense(event.getId(), expenseToUpdate);
                }
                
                server.deleteParticipant(event.getId(), participant);


                // Show confirmation message
                Alert deleteConfirmation = new Alert(Alert.AlertType.INFORMATION);
                deleteConfirmation.setTitle("Participant Deleted");
                deleteConfirmation.setHeaderText(null);
                deleteConfirmation.setContentText("Participant deleted successfully!");
                deleteConfirmation.showAndWait();

                mainCtrl.goToOverview();
            }
        });
    }

    /**
     *  swithces to name text field
     */

    public void switchToNameTextField() {
        nicknameLabel.setVisible(false);
        nicknameTextField.setVisible(true);
        nicknameTextField.requestFocus();
    }

    /**
     *   switches to email txt field
     */
    public void switchToEmailTextField() {
        emailLabel.setVisible(false);
        emailTextField.setVisible(true);
        emailTextField.requestFocus();
    }

    /**
     *
     */

    public void switchToBicTextField() {
        bicLabel.setVisible(false);
        bicTextField.setVisible(true);
        bicTextField.requestFocus();
    }

    /**
     *
     */
    public void switchToIbanTextField() {
        ibanLabel.setVisible(false);
        ibanTextField.setVisible(true);
        ibanTextField.requestFocus();
    }

    @FXML
    private void updateNickname() {
        updateAndSwitchToLabel(nicknameTextField, nicknameLabel);
    }

    @FXML
    private void updateEmail() {
        updateAndSwitchToLabel(emailTextField, emailLabel);
    }



    @FXML
    private void updateBic() {
        updateAndSwitchToLabel(bicTextField, bicLabel);
    }

    @FXML
    private void updateIban() {
        updateAndSwitchToLabel(ibanTextField, ibanLabel);
    }

    private void updateAndSwitchToLabel(TextField textField, Label label) {
        label.setText(textField.getText());
        label.setVisible(true);
        textField.setVisible(false);
    }
    @FXML
    private void updateParticipant() {
        String newNickname = nicknameTextField.getText();
        String newEmail = emailTextField.getText();
        String newBic = bicTextField.getText();
        String newIban = ibanTextField.getText();

        if (!newNickname.equals("No nickname added")) {
            participant.setNickname(newNickname);
        }
        if (!newEmail.equals("No email added")) {
            participant.setEmail(newEmail);
        }
        if (!newBic.equals("No BIC added")) {
            participant.setBic(newBic);
        }
        if (!newIban.equals("No IBAN added")) {
            participant.setIban(newIban);
        }


            server.updateParticipant(event.getId(), participant);
            mainCtrl.goToOverview();

    }

    /**
     * goes to overview page
     * @param actionEvent
     */


    public void goToOverview(ActionEvent actionEvent) {
        mainCtrl.goToOverview();
    }

    /**
     * Changes the language of the site
     * @param event
     */
    @FXML
    public void changeLanguage(javafx.event.ActionEvent event) {
        RadioMenuItem selectedLanguageItem = (RadioMenuItem) event.getSource();
        String language = selectedLanguageItem.getText().toLowerCase();

        // Load the appropriate resource bundle based on the selected language
        MainCtrl.resourceBundle = ResourceBundle.getBundle("messages_" 
        + language, new Locale(language));
        
        Main.config.setLanguage(language);

        // Update UI elements with the new resource bundle
        updateUIWithNewLanguage();
    }

    /**
     * changes the currency to whatever is selected
     * @param event
     */
    @FXML
    public void changeCurrency(ActionEvent event) {
        RadioMenuItem selectedCurrencyItem = (RadioMenuItem) event.getSource();
        String currency = selectedCurrencyItem.getText();

        // Set the selected currency as the currency used for exchange rates
        Currency.setCurrencyUsed(currency.toUpperCase());

        // Print confirmation message
        System.out.println("Currency changed to: " + currency);
    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
        
    }
}

