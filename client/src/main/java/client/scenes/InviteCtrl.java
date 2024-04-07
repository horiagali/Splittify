package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.EmailUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Mail;
import commons.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InviteCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField inviteCodeTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button sendButton;
    @FXML
    private FlowPane emailFlowPane;
    @FXML
    private Menu currencyMenu;
    @FXML
    private Button backButton;
    @FXML
    private Event event;
    private final ObservableList<String> emailList = FXCollections.observableArrayList();
    private final Set<String> uniqueEmails = new HashSet<>();
    private boolean sendingInProgress = false;
    @FXML
    private ToggleGroup currencyGroup;
    @FXML
    private ImageView languageFlagImageView;

    @FXML
    private ToggleGroup languageGroup;
    @FXML
    private Text invitePeopleText;
    @FXML
    private Label eventCodeText;
    @FXML
    private Label inviteByMailText;
    @FXML
    private Button addButton;
    @FXML
    private Button copyButton;
    private Executor executor;


    /**
     * Constructor for the InviteCtrl class.
     *
     * @param server   ServerUtils instance for server communication.
     * @param mainCtrl MainCtrl instance for controlling main scene navigation.
     */
    @Inject
    public InviteCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     *
     * @param url            url
     * @param resourceBundle The resources used to localize the root object,
     *                       or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generateInviteCode();
        addKeyboardNavigationHandlers();
        executor = Executors.newVirtualThreadPerTaskExecutor();
        if (!goodCredentials()) {
            sendButton.setDisable(true);
            sendButton.setStyle("--body-background-color: grey;");
        }
    }
    /**
     * good credentials
     * @return true if good, false otherwise
     */
    private boolean goodCredentials(){
        if (EmailUtils.getHost() == null || EmailUtils.getPort() == null ||
                EmailUtils.getPassword() == null || EmailUtils.getUsername() == null)
            return false;
        return isValidEmail(EmailUtils.getUsername());
    }


    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.Q) {
                copyInviteCode();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.W) {
                addEmail();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                sendInvitationsByEmail();
            }
        });
    }

    /**
     * Generates an invitation code and copies it to the system clipboard.
     */
    @FXML
    private void generateInviteCode() {
        String inviteCode = getInviteCode();
        inviteCodeTextField.setText(inviteCode);
        System.out.println("Invite Code: " + inviteCode);
    }

    /**
     * Generates a unique invite code.
     * @return the invitation code
     */
    private String getInviteCode() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            return selectedEvent.getId().toString();
        }
        else return null;
    }

    /**
     * Refreshes invite code
     */
    @FXML
    public void refresh() {
        generateInviteCode();
    }

    /**
     * Copies the generated invite code to the clipboard.
     */
    @FXML
    private void copyInviteCode() {
        String inviteCode = inviteCodeTextField.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(inviteCode);
        clipboard.setContent(content);
    }


    /**
     * Adds a new email address to the list if it is valid and unique.
     */
    @FXML
    private void addEmail() {
        String email = emailTextField.getText().trim();
        if (isValidEmail(email) && !uniqueEmails.contains(email)) {
            emailList.add(email);
            uniqueEmails.add(email);
            updateEmailListUI();
            emailTextField.clear();
        } else {
            showErrorDialog("Invalid email address or email already exists!");
        }
    }

    /**
     * Update the UI for displaying email addresses.
     */
    private void updateEmailListUI() {
        emailFlowPane.getChildren().clear();
        for (String email : emailList) {
            Label emailLabel = new Label(email);
            emailLabel.setStyle("-fx-padding: 5px;");

            // Load the image
            Image removeImage = new Image(Objects.requireNonNull(getClass().
                    getResourceAsStream("/client/scenes/images/removeEmail.png")));

            // Create an ImageView with the image
            Button removeButton = getButton(email, removeImage);
            HBox hbox = new HBox(emailLabel, removeButton);
            hbox.setSpacing(5);
            hbox.setAlignment(Pos.CENTER_LEFT);

            emailFlowPane.getChildren().add(hbox);
        }
        sendButton.setDisable(emailList.isEmpty());
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
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
        updateUIWithNewLanguage();

    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.sendInvites"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        invitePeopleText.setText(MainCtrl.resourceBundle.getString("Text.invitePeopleText"));
        eventCodeText.setText(MainCtrl.resourceBundle.getString("Text.eventCodeText"));
        inviteCodeTextField.setPromptText(MainCtrl.resourceBundle.getString("Text.inviteCode"));
        invitePeopleText.setText(MainCtrl.resourceBundle.getString("Text.inviteByMailText"));
        emailTextField.setPromptText(MainCtrl.resourceBundle.getString("Text.email"));
        copyButton.setText(MainCtrl.resourceBundle.getString("button.copy"));
        addButton.setText(MainCtrl.resourceBundle.getString("button.add"));
        sendButton.setText(MainCtrl.resourceBundle.getString("button.send"));
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
     * Get button
     * @param email email
     * @param removeImage the x
     * @return button
     */
    private Button getButton(String email, Image removeImage) {
        ImageView removeIcon = new ImageView(removeImage);
        removeIcon.setFitWidth(5);
        removeIcon.setFitHeight(5);

        // Create a button and set the ImageView as its graphic
        Button removeButton = new Button();
        removeButton.setGraphic(removeIcon);
        removeButton.setOnAction(event -> removeEmail(email));
        removeButton.setPrefSize(10, 10);

        Insets smallerInsets = new Insets(3);
        CornerRadii roundedCorners = new CornerRadii(10);

        removeButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,
                roundedCorners, smallerInsets)));
        return removeButton;
    }

    /**
     *  Method to remove an email address
     *  @param  email address to be removed
     */
    private void removeEmail(String email) {
        uniqueEmails.remove(email);
        emailList.remove(email);
        uniqueEmails.remove(email);
        updateEmailListUI();
    }

    /**
     * Sends invitations to the entered email addresses.
     */
    @FXML
    private void sendInvitationsByEmail() {
        if (!sendingInProgress) {
            sendingInProgress = true;
            sendButton.setDisable(true);
            for (String email : emailList){
                Mail mail = new Mail(email,event.getTitle(), "The invite code is: " +
                        event.getId().toString());
                server.addParticipant(event.getId(), new Participant(email, email, "", "", 0));
                executor.execute(() -> EmailUtils.sendEmail(mail));
            }
            emailList.clear();
            uniqueEmails.clear();
            updateEmailListUI();
            sendingInProgress = false;
            sendButton.setDisable(false);
            anchorPane.requestFocus();
        }
    }

    /**
     * Checks whether email is valid
     * @param email email to check
     * @return true iff valid
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*" +
                "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Navigates back to the overview scene.
     */
    @FXML
    public void back() {
        mainCtrl.goToOverview();
    }

    /**
     * setter for the event
     * @param event an Event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Displays an error dialog with the given message.
     *
     * @param message The error message to be displayed.
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        System.out.println(MainCtrl.resourceBundle.getString("Text.currencyChangedTo") + currency);
    }
}
