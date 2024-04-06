package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;


public class EditEventCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private Menu languageMenu;
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private ToggleGroup currencyGroup;
    @FXML
    private Button backButton;
    @FXML
    private Text titleLabel;
    @FXML
    private Text descriptionLabel;
    @FXML
    private Text locationLabel;
    @FXML
    Button editButton;

    Event event;

    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EditEventCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addKeyboardNavigationHandlers();
    }

    /**
     * loads the info of the event
     *
     * @param event
     */
    public void loadInfo(Event event){

    }

    /**
     * resets to values of creating a participant
     */
    public void reset() {
    
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
                editEvent();
            }
        });
    }

    /**
     * edits event 
     */
    public void editEvent() {

    }

    /**
     *
     */

    public void goToEventOverview() {
        mainCtrl.goToOverview();
    }


    private void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Changes the language of the site
     *
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
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        titleField.setText(MainCtrl.resourceBundle.getString("Text.name") + ": ");
        titleField.setText("Email:");
        titleField.setText("IBAN:");
        titleField.setText("BIC:");
        titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter")
                + " " + MainCtrl.resourceBundle.getString("Text.name"));
        titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " Email");
                titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " IBAN");
                titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " BIC");
    }

    /**
     * changes the currency to whatever is selected
     *
     * @param event
     */
    @FXML
    public void changeCurrency(ActionEvent event) {
        RadioMenuItem selectedCurrencyItem = (RadioMenuItem) event.getSource();
        String currency = selectedCurrencyItem.getText();

        // Set the selected currency as the currency used for exchange rates
        Currency.setCurrencyUsed(currency.toUpperCase());

        // Print confirmation message
        System.out.println(MainCtrl.resourceBundle.getString("Text.currencyChangedTo") + currency);
    }
}
