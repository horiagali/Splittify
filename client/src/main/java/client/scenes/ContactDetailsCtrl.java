package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;


public class ContactDetailsCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane anchorPane;

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
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addKeyboardNavigationHandlers();
    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                goToEventOverview();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.P) {
                ok();
            }
        });
    }

    /**
     * stop filling in fields
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
     */

    public void goToEventOverview() {
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
