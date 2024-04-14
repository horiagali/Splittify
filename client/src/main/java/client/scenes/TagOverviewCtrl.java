package client.scenes;

import java.awt.ScrollPane;
import java.net.URL;
import java.util.*;


import com.google.inject.Inject;


import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TagOverviewCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Event event;
    private static boolean isActive;
    private List<Tag> tags;

    @FXML
    private Menu languageMenu;

    @FXML
    private Button back;
    @FXML
    private ImageView languageFlagImageView;

    @FXML
    private ScrollPane tagsScrollPane;

    @FXML
    private FlowPane flowPane;

    @FXML
    private ToggleGroup currencyGroup;

    @FXML
    private VBox tagInfo;
    @FXML
    private VBox vbox;

    TextField name;
    @FXML
    private Text tagsText;

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
     * sets if scene is active or not
     * @param bool true if active, false otherwise
     */
    public static void setIsActive(boolean bool) {
        isActive = bool;
    }

    /**
     * refreshes the data
     * uses rounding to ensure percentages round to 100%
     */
    public void refresh() {
        tagInfo.getChildren().clear();
        loadTags();
        addKeyboardNavigationHandlers();
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
        updateFlagImageURL(language);
        updateUIWithNewLanguage();

        refresh();
    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.tagManager"));
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        String stageTitleString = "title.statistics";
        if (event != null)
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString)
                    + event.getTitle());
        else
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString));
        tagsText.setText(MainCtrl.resourceBundle.getString("Text.tagText"));
        
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
     * Loads tags into page from DB
     */
    private void loadTags() {
        if (event != null) {
            List<Tag> tags =
                    server.getTags(OverviewCtrl.getSelectedEvent().getId());
            ArrayList<String> visited = new ArrayList();
            for(int i = tags.size() - 1; i >= 0; i--) {
                if(!visited.contains(tags.get(i).getName())) {
                    visited.add(tags.get(i).getName());
                } else {
                    server.deleteTag(event.getId(), tags.get(i).getId());
                }
            }
            tags = server.getTags(event.getId());

            Tag toRemove = tags.stream().filter(x -> x.getName()
            .equals("gifting money")).toList().get(0);
            tags.remove(toRemove);
            flowPane.getChildren().clear();
            for (Tag tag : tags) {
                Button tagButton = setStyle(tag);
                tagButton.setMaxWidth(300);
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
                    createNewTag();
                }
            });
            flowPane.getChildren().add(newTagButton);
        }
    }

    /**
     * gives the menu for creating a new tag.
     */
    public void createNewTag() {
        Tag newTag = new Tag();
        newTag.setName("new tag");
        newTag.setEvent(event);
        tagInfo.getChildren().clear();
        Button button = new Button("new tag");
        button.setTextFill(Color.BLACK);
        String style = "-fx-background-color: " + "#d9dbd9" + ";" +
                "-fx-font-size: " + 20 + ";" +
                "-fx-border-radius: " + 10 + ";";
        button.setStyle(style);
        tagInfo.getChildren().add(button);
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setId("colorpickerCreate");
        colorPickerCreateTag(newTag, button, colorPicker);
        tagInfo.getChildren().add(colorPicker);
        name = new TextField("new tag");
        name.setStyle("-fx-font-size: " + 20 + ";");
        name.setAlignment(Pos.CENTER);
        name.setOnAction(event -> {
            newTag.setName(name.getText());
            button.setText(name.getText());
        });
        name.setOnMouseExited(event -> {
            newTag.setName(name.getText());
            button.setText(name.getText());
        });
        tagInfo.getChildren().add(name);
        Button createButton = new Button(MainCtrl.resourceBundle.getString("button.createTag"));
        createButton.setOnAction(event -> {
            if(!server.getTags(TagOverviewCtrl.event.getId())
                    .stream()
                    .map(Tag::getName).toList().contains(name.getText())) {
                newTag.setName(name.getText());
                server.addTag(TagOverviewCtrl.event.getId(), newTag);

                // Update last change date
                TagOverviewCtrl.event.setDate(new Date());
                server.updateEvent(TagOverviewCtrl.event);

                refresh();
            } else {
                Text text = new Text(MainCtrl.resourceBundle.getString("Text.tagAlreadyExists"));
                text.setFill(Color.RED);
                tagInfo.getChildren().add(text);
            }
        });
        tagInfo.getChildren().add(createButton);

    }

    private void colorPickerCreateTag(Tag newTag, Button button, ColorPicker colorPicker) {
        String colorString = "#d9dbd9";
        double red = Integer.decode(colorString.substring(0, 3));
        double green = Integer.decode("#" + colorString.substring(3, 5));
        double blue = Integer.decode("#" + colorString.substring(5, colorString.length()));
        Color color = new Color(red/255, green/255, blue/255, 1);
        colorPicker.setValue(color);
        newTag.setColor("#d9dbd9");
        colorPicker.setOnAction(event -> {
            String hex = String.format("#%02X%02X%02X",
                    (int) (colorPicker.getValue().getRed() * 255),
                    (int) (colorPicker.getValue().getGreen() * 255),
                    (int) (colorPicker.getValue().getBlue() * 255));
            button.setStyle("-fx-background-color: " + hex + ";" +
                    "-fx-font-size: " + 20 + ";" +
                    "-fx-border-radius: " + 10 + ";");
            newTag.setColor(hex);
        });
    }

    @FXML
    private void deleteTag(Tag tag) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(MainCtrl.resourceBundle.getString("Text.delete")
                + " " + MainCtrl.resourceBundle.getString("Text.tag"));
        alert.setHeaderText(MainCtrl.resourceBundle.getString("Text.areYouSureTag"));
        alert.setContentText(MainCtrl.resourceBundle.getString("Text.warningDeleteTag"));

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Delete the tag from the server
                List<Expense> list = server.getExpensesByEventId(this.event.getId()).stream()
                        .filter(x -> x.getTag().getName().equals(tag.getName())).toList();
                System.out.println(list);
                for (Expense expense : list) {
                    expense.setTag(server.getTags(this.event.getId()).get(0));
                    server.updateExpense(this.event.getId(), expense);
                }

                server.deleteTag(event.getId(), tag.getId());

                // Update last change date
                event = server.getEvent(event.getId());
                event.setDate(new Date());
                server.updateEvent(event);

                refresh();

                // Show confirmation message
                Alert deleteConfirmation = new Alert(Alert.AlertType.INFORMATION);
                deleteConfirmation.setTitle(MainCtrl.resourceBundle.getString("Text.tagDeleted"));
                deleteConfirmation.setHeaderText(null);
                deleteConfirmation.setContentText
                        (MainCtrl.resourceBundle.getString("Text.tagDeleted") + " "
                                + MainCtrl.resourceBundle.getString("Text.successfully"));
                deleteConfirmation.showAndWait();
            }
        });
    }


    /**
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
            tag.setEvent(TagOverviewCtrl.event);
            showTagInfo(new Tag(tag.getName(), tag.getColor()), tag);
        }
        });
        return button;
    }


    /**
     * shows info of tag to edit or remove it
     *
     * @param oldTag the old version of this tag
     * @param newTag the new version of this tag
     */
    private void showTagInfo(Tag oldTag, Tag newTag) {
        tagInfo.getChildren().clear();
        if (oldTag.getName().equals("no tag")) {
            noTagSelected(oldTag, newTag);
            return;
        }
        Button originalTag = new Button(oldTag.getName());
        originalTag.setTextFill(Color.BLACK);
        Button newTagButton = new Button(newTag.getName());
        newTagButton.setTextFill(Color.BLACK);
        newTagButton.setStyle("-fx-background-color: " + newTag.getColor() + ";" +
                "-fx-font-size: " + 20 + ";" +
                "-fx-border-radius: " + 10 + ";");
        originalTag.setStyle("-fx-background-color: " + oldTag.getColor() + ";" +
                "-fx-font-size: " + 20 + ";" + "-fx-border-radius: " + 10 + ";");
        Text text = new Text("↓");
        text.setStyle("-fx-font-size: 20");
        tagInfo.getChildren().add(originalTag);
        tagInfo.getChildren().add(text);
        tagInfo.getChildren().add(newTagButton);
        ColorPicker colorPicker = new ColorPicker();
        colorPickerLogic(oldTag, newTag, colorPicker);

        tagInfo.getChildren().add(colorPicker);
        name = new TextField(newTag.getName());
        name.setStyle("-fx-font-size: " + 20 + ";");
        name.setAlignment(Pos.CENTER);
        name.setOnMouseExited(event -> {
            newTag.setName(name.getText());
            showTagInfo(oldTag, newTag);
        });
        tagInfo.getChildren().add(name);
        HBox hbox = deleteUpdateButtons(newTag, oldTag);
        tagInfo.getChildren().add(hbox);
        tagInfo.setOpacity(1);
    }

    /**
     * if the tag 'no tag' is selected. Is different because name cannot be changed.
     *
     * @param oldTag
     * @param newTag
     */
    private void noTagSelected(Tag oldTag, Tag newTag) {
        Button originalTag = new Button(oldTag.getName());
        originalTag.setTextFill(Color.BLACK);
        originalTag.setStyle("-fx-background-color: " + newTag.getColor() + ";" +
                "-fx-font-size: " + 20 + ";" +
                "-fx-border-radius: " + 10 + ";");
        ColorPicker colorPicker = new ColorPicker();
        colorPickerLogic(oldTag, oldTag, colorPicker);
        tagInfo.getChildren().add(originalTag);
        tagInfo.getChildren().add(colorPicker);
        Button update = new Button(MainCtrl.resourceBundle.getString("button.updateTag"));
        update.setOnAction(event -> {
            oldTag.setEvent(this.event);
            oldTag.setId(server.getTags(this.event.getId()).get(0).getId());
            server.updateTag(oldTag, this.event.getId());
            refresh();
        });
        tagInfo.getChildren().add(update);
    }

    private void colorPickerLogic(Tag oldTag, Tag newTag, ColorPicker colorPicker) {
        String colorString = newTag.getColor();
        double red = Integer.decode(colorString.substring(0, 3));
        double green = Integer.decode("#" + colorString.substring(3, 5));
        double blue = Integer.decode("#" + colorString.substring(5, colorString.length()));
        Color color = new Color(red / 255, green / 255, blue / 255, 1);
        colorPicker.setValue(color);
        colorPicker.setOnAction(event -> {
            String hex = String.format("#%02X%02X%02X",
                    (int) (colorPicker.getValue().getRed() * 255),
                    (int) (colorPicker.getValue().getGreen() * 255),
                    (int) (colorPicker.getValue().getBlue() * 255));
            newTag.setColor(hex);
            showTagInfo(oldTag, newTag);
        });
    }

    private HBox deleteUpdateButtons(Tag newTag, Tag oldTag) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        Button delete = new Button(MainCtrl.resourceBundle.getString("button.deleteTag"));
        delete.setOnAction(event -> {
            deleteTag(newTag);
        });
        Button update = new Button(MainCtrl.resourceBundle.getString("button.updateTag"));
        update.setOnAction(event -> { 
            if(oldTag.getName().equals(name.getText()) ||
                !server.getTags(this.event.getId()).stream()
                .map(x -> x.getName()).toList().contains(name.getText()))
            {
                newTag.setName(name.getText());
                server.updateTag(newTag, this.event.getId());

                // Update last change date
                TagOverviewCtrl.event.setDate(new Date());
                server.updateEvent(TagOverviewCtrl.event);

                refresh();
            } else {
                Text text = new Text(MainCtrl.resourceBundle.getString("Text.tagNameExists"));
                text.setFill(Color.RED);
                tagInfo.getChildren().add(text);
            }
        });
        hbox.getChildren().add(update);
        hbox.getChildren().add(delete);
        return hbox;
    }

    private void addHoverAnimation(Node node) {
        double startX = node.getScaleX();
        double startY = node.getScaleY();
        double startZ = node.getScaleZ();
        node.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), node);
            scaleTransition.setFromX(startX);
            scaleTransition.setFromY(startY);
            scaleTransition.setFromZ(startZ);
            scaleTransition.setToX(node.getScaleX() * 1.1);
            scaleTransition.setToY(node.getScaleY() * 1.1);
            scaleTransition.setToZ(node.getScaleZ() * 1.1);
            scaleTransition.play();

        });
        node.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), node);
            scaleTransition.setFromX(node.getScaleX());
            scaleTransition.setFromY(node.getScaleY());
            scaleTransition.setFromZ(node.getScaleZ());
            scaleTransition.setToX(startX);
            scaleTransition.setToY(startY);
            scaleTransition.setToZ(startZ);
            scaleTransition.play();

        });
    }


    /**
     * set the selected event to see statistics from.
     *
     * @param selectedEvent
     */
    public static void setEvent(Event selectedEvent) {
        TagOverviewCtrl.event = selectedEvent;
    }


    /**
     * back button
     */
    public void back() {
        setIsActive(false);
        tagInfo.getChildren().clear();
        mainCtrl.goToOverview();
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
        System.out.println(MainCtrl.resourceBundle.getString
                ("Text.currencyChangedTo") + ": " + currency);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        server.registerForEvents("/topic/events", e -> {
            if (event != null && e.equals(event.getId())) {
                Platform.runLater(() -> {
                    refresh();
                });
            }
        });
    }


    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        vbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.L) {
                languageMenu.show();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                refresh();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                createNewTag();
            }
        });
    }
}


