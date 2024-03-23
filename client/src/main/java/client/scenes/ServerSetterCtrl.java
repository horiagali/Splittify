package client.scenes;

import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerSetterCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    
    @FXML
    private ToggleGroup languageGroup;

    @FXML
    private Button connectToServerButton;
    @FXML
    public TextField serverURL;
    @FXML
    private Menu languageMenu;

    @FXML 
    public Stage primaryStage;

    @FXML 
    public Label invalidURL;

    @FXML 
    public Text enterUrl;




    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public ServerSetterCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;

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
     * 
     */
    @FXML
    public void changeColor() {
        connectToServerButton.setStyle("-fx-background-color: green");;
    }

    /**
     * 
     */
    @FXML
    public void changeColorBack() {
        connectToServerButton.setStyle("-fx-background-color: orange");;
    }

    /**
     * 
     */
    @FXML
    public void connect() {
        Main.config.setServerUrl(serverURL.getText());
        System.out.println("server changed to " + Main.config.getServerUrl());
        if(!Main.checkConnection()) {
            System.out.println("Please input a valid server.");
            invalidURL.setText(MainCtrl.resourceBundle.getString("Text.invalidUrl"));
        }
        else {
            invalidURL.setText("");
            mainCtrl.getMain().loadScenes();
        }
    }


    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        connectToServerButton.setText(MainCtrl.resourceBundle.getString("button.connectToServer"));
        invalidURL.setText("");
        enterUrl.setText(MainCtrl.resourceBundle.getString("Text.enterUrl"));
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
    }

    /**
     *
     */
    public void page() {
        mainCtrl.showPage();
    }

    /**
     *
     */
    public void backToEventOverview() {
        mainCtrl.goToOverview();
    }


}