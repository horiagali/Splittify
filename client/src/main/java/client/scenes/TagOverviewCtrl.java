package client.scenes;

import java.awt.ScrollPane;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.layout.FlowPane;

public class TagOverviewCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Event event;

    
    @FXML
    private Menu languageMenu;

    @FXML
    private Button back;

    @FXML
    private ScrollPane tagsScrollPane;

    @FXML
    private FlowPane flowPane;

    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public TagOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;

    }

    /**
     * refreshes the data
     * uses rounding to ensure percentages round to 100%
     */
    public void refresh() {
        loadTags();

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
    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        String stageTitleString = "title.statistics";
        if(event !=null){
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString)+event.getTitle());
        }
        else{mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString));}
        
    }

    /**
     * Loads tags into page from DB
     */
    private void loadTags() {
        if (event != null) {
            List<Tag> tags =
                    server.getTags(OverviewCtrl.getSelectedEvent().getId());

            flowPane.getChildren().clear();

            for (Tag tag : tags) {
                Button tagButton = setStyle(tag);
                flowPane.getChildren().add(tagButton);
            }
        }
    }

    /**d
     * sets the style of the button to 
     * @param tag
     * @return button of tag
     */
    public Button setStyle(Tag tag) {
        Button button = new Button(tag.getName());
        button.setTextFill(Color.BLACK);
        // if(tag.getColor() != null)
        button.setStyle("-fx-background-color: " + tag.getColor());
        button.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            System.out.println(button.getText());
        }
        });
        return button;
    }

    /**
     * set the selected event to see statistics from.
     * @param selectedEvent
     */
    public static void setEvent(Event selectedEvent) {
        TagOverviewCtrl.event = selectedEvent;
    }

    /**
     * back button
     */
    public void back(){
        mainCtrl.goToOverview();
    }
}
