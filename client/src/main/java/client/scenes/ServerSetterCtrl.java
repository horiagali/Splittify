package client.scenes;

import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ToggleGroup currencyGroup;
    @FXML
    private ImageView languageFlagImageView;
    @FXML 
    public Stage primaryStage;
    @FXML
    public Menu currencyMenu;

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
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
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
            server.checkConnectionForWebsockets();
            mainCtrl.getMain().loadScenes();
        }
    }


    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.serverSetter"));
        connectToServerButton.setText(MainCtrl.resourceBundle.getString("button.connectToServer"));
        invalidURL.setText("");
        enterUrl.setText(MainCtrl.resourceBundle.getString("Text.enterUrl"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
    }

    /**
     * Updates the flag image URL based on the selected language.
     *
     * @param language The selected language.
     */
    public void updateFlagImageURL(String language) {
        String flagImageUrl = ""; // Initialize with the default image URL
        switch (language) {
            case "english":
                flagImageUrl = "/client/scenes/images/BritishFlag.png";
                break;
            case "romana":
                flagImageUrl = "/client/scenes/images/RomanianFlag.png";
                break;
            case "nederlands":
                flagImageUrl = "/client/scenes/images/DutchFlag.png";
                break;
        }
        languageFlagImageView.setImage(new Image(getClass().getResourceAsStream(flagImageUrl)));
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
