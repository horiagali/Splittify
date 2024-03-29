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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

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

    @FXML
    private ToggleGroup currencyGroup;

    @FXML
    private VBox tagInfo;

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
            addHoverAnimation(newTagButton);
            newTagButton.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    mainCtrl.goToEditTag(this.event, null);
                }
                });
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
        addHoverAnimation(button);
        button.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            showTagInfo(tag);
        }
        });
        return button;
    }

    /**
     * shows info of tag to edit or remove it
     * @param tag
     */
    private void showTagInfo(Tag tag) {
        Button button = new Button(tag.getName());
        button.setTextFill(Color.BLACK);
        button.setStyle("-fx-background-color: " + tag.getColor() + ";" +
        "-fx-font-size: " + 20 + ";" +
        "-fx-border-radius: " + 10 + ";");
        tagInfo.getChildren().clear();
        tagInfo.getChildren().add(button);
        ColorPicker colorPicker = new ColorPicker();
        String colorString = tag.getColor();
        double red = Integer.decode(colorString.substring(0, 3));
        double green = Integer.decode("#" + colorString.substring(3, 5));
        double blue = Integer.decode("#" + colorString.substring(5, colorString.length()));
        Color color = new Color(red/255, green/255, blue/255, 1);
        colorPicker.setValue(color);

        colorPicker.setOnAction(event -> {
            String redColor = Integer.toHexString((int) 
            Math.round(colorPicker.getValue().getRed() * 255));
            String greenColor = Integer.toHexString((int) 
            Math.round(colorPicker.getValue().getGreen() * 255));
            String blueColor = Integer.toHexString((int) 
            Math.round(colorPicker.getValue().getBlue() * 255));
            System.out.println(redColor + " " + greenColor + " " + blueColor);
            if(greenColor.length() == 1)
            greenColor = "0" + greenColor;
            if(redColor.length() == 1)
            redColor = "0" + redColor;
            if(blueColor.length() == 1)
            blueColor = "0" + blueColor;
            String hex = "#" + redColor + greenColor + blueColor;
            tag.setColor(hex);
            showTagInfo(tag);
            // button.setStyle("-fx-background-color: " + hex + ";" + "-fx-font-size: " + 20 + ";" +
            // "-fx-border-radius: " + 10 + ";");

            });
        tagInfo.getChildren().add(colorPicker);
        tagInfo.setOpacity(1);
        loadTags();
    }

    private void addHoverAnimation(Node node) {
        node.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), node);
            scaleTransition.setFromX(node.getScaleX());
            scaleTransition.setFromY(node.getScaleY());
            scaleTransition.setFromZ(node.getScaleZ());
            scaleTransition.setToX(node.getScaleX()*1.1);
            scaleTransition.setToY(node.getScaleY()*1.1);
            scaleTransition.setToZ(node.getScaleZ()*1.1);
            scaleTransition.play();

        });
        node.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), node);
            scaleTransition.setFromX(node.getScaleX());
            scaleTransition.setFromY(node.getScaleY());
            scaleTransition.setFromZ(node.getScaleZ());
            scaleTransition.setToX(node.getScaleX()/1.1);
            scaleTransition.setToY(node.getScaleY()/1.1);
            scaleTransition.setToZ(node.getScaleZ()/1.1);
            scaleTransition.play();

        });
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
        tagInfo.setOpacity(0);
        mainCtrl.goToOverview();
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

