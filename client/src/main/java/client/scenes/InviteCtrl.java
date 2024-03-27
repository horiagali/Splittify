package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Mail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
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
    private Event event;
    private final ObservableList<String> emailList = FXCollections.observableArrayList();
    private final Set<String> uniqueEmails = new HashSet<>();
    private boolean sendingInProgress = false;

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
        String inviteCode = generateRandomCode();
        inviteCodeTextField.setText(inviteCode);
        System.out.println("Invite Code: " + inviteCode);
    }

    /**
     * Generates a unique invite code.
     * @return the invitation code
     */
    private String generateRandomCode() {
        // we still have to make them unique somehow?
        StringBuilder randomChars = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 6;
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            randomChars.append(characters.charAt(index));
        }
        return randomChars.toString();
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
            System.out.println("Invalid email address or email already exists!");
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
            // Disable the sendButton
            sendButton.setDisable(true);
            for (String email : emailList){
                Mail mail = new Mail(email,event.getTitle(), "The invite code is: " +
                        event.getId().toString());
                server.sendEmail(mail);
            }


            sendingInProgress = false;
            sendButton.setDisable(false);
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
}
