package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
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
            nicknameLabel.setText(participant.getNickname());
            emailLabel.setText(participant.getEmail());
            balanceLabel.setText(String.valueOf(participant.getBalance()));
            bicLabel.setText(participant.getBic());
            ibanLabel.setText(participant.getIban());

            // Check for empty values
            checkEmptyFields();
        }
    }

    private void checkEmptyFields() {
        if (participant.getNickname() == null) {
            errorMessageLabel.setText("Nickname is empty");
        }
        if (participant.getEmail() == null) {
            errorMessageLabel.setText("Email is empty");
        }

        if (participant.getBic() == null) {
            errorMessageLabel.setText("BIC is empty");
        }
        if (participant.getIban() == null) {
            errorMessageLabel.setText("IBAN is empty");
        }
    }

    @FXML
    private void deleteParticipant() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Participant");
        alert.setHeaderText("Are you sure you want to delete this participant?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Delete the participant from the server
                server.deleteParticipant(event.getId(), participant);

                // Update UI to reflect deletion
                // For simplicity, you can just go back to the overview scene
                mainCtrl.goToOverview();

                // Show confirmation message
                Alert deleteConfirmation = new Alert(Alert.AlertType.INFORMATION);
                deleteConfirmation.setTitle("Participant Deleted");
                deleteConfirmation.setHeaderText(null);
                deleteConfirmation.setContentText("Participant deleted successfully!");
                deleteConfirmation.showAndWait();
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
        participant.setNickname(nicknameLabel.getText());
        participant.setEmail(emailLabel.getText());
        participant.setBic(bicLabel.getText());
        participant.setIban(ibanLabel.getText());

        server.updateParticipant(event.getId(), participant);
        mainCtrl.goToOverview();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update Participant");
        alert.setHeaderText(null);
        alert.setContentText("Participant details updated successfully!");
        alert.showAndWait();
    }
}

