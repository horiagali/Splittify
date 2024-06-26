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
    private Menu currencyMenu;
    @FXML
    Button editButton;
    @FXML
    private Text title;

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
     */
    public void loadInfo(){
        titleField.clear();
        locationField.clear();
        descriptionField.clear();
        updateUIWithNewLanguage();
    }

    /**
     * resets to values of creating a participant
     */
    public void reset() {
    
    }

    /**
     * setter for event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
        loadInfo();
    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                goToEventOverview();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                editEvent();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.L) {
                languageMenu.show();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.M) {
                currencyMenu.show();
            }
        });
    }

    /**
     * edits event 
     */
    public void editEvent() {
        String eventTitle = event.getTitle();
        if(!titleField.getText().equals("")) {
            eventTitle = titleField.getText();
        }
        
        String eventDescription = event.getDescription();
        if(!descriptionField.getText().equals("")) {
            eventDescription = descriptionField.getText();
        }

        String eventLocation = event.getLocation();
        if(!locationField.getText().equals("")) {
            eventLocation = locationField.getText();
        }
        // event.setTitle("resetting purposes");
        // event.setDescription("resetting purposes");
        // event.setLocation("resetting purposes");
        server.updateEvent(event);
        event.setTitle(eventTitle);
        event.setDescription(eventDescription);
        event.setLocation(eventLocation);
        server.updateEvent(event);
        mainCtrl.showEventOverview(event);

    }

    /**
     * goes back to overview
     */
    public void back() {
        mainCtrl.showOverview();
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
        mainCtrl.updateLanguage(language);
        updateUIWithNewLanguage();
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
        title.setText(MainCtrl.resourceBundle.getString("Text.editEvent"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        editButton.setText(MainCtrl.resourceBundle.getString("Text.editEvent"));

        titleLabel.setText(MainCtrl.resourceBundle.getString("Text.title"));
        descriptionLabel.setText(MainCtrl.resourceBundle.getString("TableColumn.colDescription"));
        locationLabel.setText(MainCtrl.resourceBundle.getString("TableColumn.colLocation"));

        titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.newTitle"));
        descriptionField.setPromptText(MainCtrl.resourceBundle.getString("Text.newDescription"));
        locationField.setPromptText(MainCtrl.resourceBundle.getString("Text.newLocation"));
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
