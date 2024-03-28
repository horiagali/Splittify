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
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
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
            Button newTagButton = new Button("+");
            String style = "-fx-background-color: #d9dbd9;" +
            "-fx-font-size: " + 20 + ";" +
            "-fx-border-radius: " + 10 + ";";
            newTagButton.setStyle(style);
            flowPane.getChildren().add(newTagButton);

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
        String style = "-fx-background-color: " + tag.getColor() + ";" +
        "-fx-font-size: " + 20 + ";" +
        "-fx-border-radius: " + 10 + ";";
        button.setStyle(style);
        button.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), button);
            scaleTransition.setFromX(button.getScaleX());
            scaleTransition.setFromY(button.getScaleY());
            scaleTransition.setFromZ(button.getScaleZ());
            scaleTransition.setToX(button.getScaleX()*1.1);
            scaleTransition.setToY(button.getScaleY()*1.1);
            scaleTransition.setToZ(button.getScaleZ()*1.1);
            scaleTransition.play();

        });
        button.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), button);
            scaleTransition.setFromX(button.getScaleX());
            scaleTransition.setFromY(button.getScaleY());
            scaleTransition.setFromZ(button.getScaleZ());
            scaleTransition.setToX(button.getScaleX()/1.1);
            scaleTransition.setToY(button.getScaleY()/1.1);
            scaleTransition.setToZ(button.getScaleZ()/1.1);
            scaleTransition.play();

        });
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
