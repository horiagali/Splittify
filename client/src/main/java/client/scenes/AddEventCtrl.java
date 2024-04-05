package client.scenes;

import java.awt.*;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

public class AddEventCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private Button addEventButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Menu currencyMenu;
    @FXML
    private ToggleGroup currencyGroup;
    @FXML
    private javafx.scene.control.Label eventNameLabel;
    @FXML
    private javafx.scene.control.Label eventLocationLabel;
    @FXML
    private javafx.scene.control.Label eventDescriptionLabel;

    /**
     * Constructor for the controller
     * @param server server
     * @param mainCtrl mainCtrl
     */
    @Inject
    public AddEventCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
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
                ActionEvent dummyEvent = new ActionEvent();
                cancel(dummyEvent);
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                ActionEvent dummyEvent = new ActionEvent();
                addEvent(dummyEvent);
            }
        });
    }

    /**
     * Adds an event directly into the database. No checking of values
     * @param ae action event
     */
    public void addEvent(ActionEvent ae) {
        if (nameField.getText().isEmpty() || nameField.getText().isBlank()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(MainCtrl.resourceBundle.getString("Text.enterTitle"));
            alert.showAndWait();
            return;
        }

        Event newEvent = new Event(
                nameField.getText(),
                descriptionField.getText(),
                locationField.getText(),
                new Date()
        );

        try {
            server.sendEvent("/app/events", newEvent);
            //server.addEvent(newEvent);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Return user to QuoteOveview
     * @param e actionEvent
     */
    public void cancel(ActionEvent e) {
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Clears all text fields
     */
    public void clearFields() {
        nameField.clear();
        descriptionField.clear();
        locationField.clear();
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
        
        mainCtrl.updateLanguage(language);

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

        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.addEvent"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
        eventNameLabel.setText(MainCtrl.resourceBundle.getString("Text.eventName"));
        eventDescriptionLabel.setText(MainCtrl.resourceBundle.getString("Text.eventDescription"));
        eventLocationLabel.setText(MainCtrl.resourceBundle.getString("Text.eventLocation"));
        addEventButton.setText(MainCtrl.resourceBundle.getString("button.createEvent"));
        cancelButton.setText(MainCtrl.resourceBundle.getString("button.cancel"));
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
        System.out.println(MainCtrl.resourceBundle.getString("Text.changedCurrencyTo") + currency);
    }
}
