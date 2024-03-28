package client.scenes;

import java.awt.ScrollPane;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


import com.google.inject.Inject;


import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import commons.Tag;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.layout.FlowPane;

public class EditTagCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Event event;
    private static Tag tag;

    
    @FXML
    private Menu languageMenu;

    @FXML
    private Button back;

    @FXML
    private ScrollPane tagsScrollPane;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ToggleGroup currencyGroup;

    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EditTagCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;

    }

    /**
     * refreshes the data
     * uses rounding to ensure percentages round to 100%
     */
    public void refresh() {

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
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.statistics") 
        + event.getTitle());
        
    }


    /**
     * set the selected event to see overview of.
     * @param selectedEvent
     */
    public static void setEvent(Event selectedEvent) {
        EditTagCtrl.event = selectedEvent;
    }

    /**
     * set the selected tag to see overview of.
     * @param selectedTag
     */
    public static void setTag(Tag selectedTag) {
        EditTagCtrl.tag = selectedTag;
    }


    /**
     * back button
     */
    public void back() {
        mainCtrl.goToTagOverview(event);
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
}

