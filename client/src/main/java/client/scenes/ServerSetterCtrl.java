package client.scenes;

import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
    public void connect() {
        Main.config.setServerUrl(serverURL.getText());
        System.out.println("server changed to " + Main.config.getServerUrl());
    }


    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {

        // createEventButton.setText(MainCtrl.resourceBundle.getString("button.createEvent"));
        // joinEventButton.setText(MainCtrl.resourceBundle.getString("button.joinEvent"));
        // refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        // adminButton.setText(MainCtrl.resourceBundle.getString("button.admin"));
        // yourEventsText.setText(MainCtrl.resourceBundle.getString("Text.yourEvents"));
        // colDate.setText(MainCtrl.resourceBundle.getString("TableColumn.colDate"));
        // colName.setText(MainCtrl.resourceBundle.getString("TableColumn.colName"));
        // colLocation.setText(MainCtrl.resourceBundle.getString("TableColumn.colLocation"));
        // languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
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
    public void goToOverview() {
        mainCtrl.goToOverview();
    }


}
