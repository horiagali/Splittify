package client.scenes;

import client.Main;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdminPassCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static String password;

    @FXML
    private PasswordField passwordField;
    @FXML
    private VBox vbox;
    @FXML
    private Menu languageMenu;


    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AdminPassCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }
    /**
     *  sets the password of the instance
     * @param pass the password generated when the page was opened
     */
    public static void setPass(String pass) {
        password = pass;
    }

    /**
     *  goes back to main page
     */
    public void goBack() {
        mainCtrl.showOverview();
    }

    /**
     *
     * @return   returns a random 8 character password
     */
    public static String generatePassword() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[8];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * checks the inputted password against the instance password, throws error if not equal
     * if equal go to admin page
     */
    @FXML
    private void tryPassword() {
        String enteredPassword = passwordField.getText();
        if (enteredPassword.equals(password)) {
            mainCtrl.goToAdminPage();
        } else {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR, "Incorrect password! >:(",
                    ButtonType.OK
            );
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addKeyboardNavigationHandlers();
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
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
    }



    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        vbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                goBack();
            }
            if (event.getCode() == KeyCode.ENTER) {
                tryPassword();
            }
        });
    }
}
