package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.ResourceBundle;

public class AdminPassCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static String password;

    @FXML
    private PasswordField passwordField;


    /**
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AdminPassCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public static void setPass(String pass) {
        password = pass;
    }

    /**
     *
     */
    public void goBack() {
        mainCtrl.showOverview();
    }


    public static String generatePassword() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[8];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @FXML
    private void tryPassword() {
        String enteredPassword = passwordField.getText();
        if (enteredPassword.equals(password)) {
            mainCtrl.goToAdminPage();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect password! >:(", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
