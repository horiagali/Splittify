package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;


public class ContactDetailsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField ibanField;
    @FXML
    private TextField bicField;

    /**
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public ContactDetailsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     *  ok button pressed
     */

    public void ok() {
        String name = nameField.getText();
        String email = emailField.getText();
        String iban = ibanField.getText();
        String bic = bicField.getText();

        if (!name.isEmpty()) {
            Participant participant = new Participant(name, email, bic, iban, 0.0);
            server.addParticipant(OverviewCtrl.getSelectedEvent().getId(),participant);


            clearFields();
            mainCtrl.goToOverview();
        } else {
            showAlert(AlertType.ERROR, "Error", "Name field is empty", "Please enter a name.");
        }
    }

    /**
     *
     * @param actionEvent
     */

    public void goToEventOverview(ActionEvent actionEvent) {
        mainCtrl.goToOverview();
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        ibanField.clear();
        bicField.clear();
    }



    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
