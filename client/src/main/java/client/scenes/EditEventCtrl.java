package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.Event;
import commons.Expense;
import commons.Participant;
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
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
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
    Text title;
    @FXML
    HBox name;
    @FXML
    HBox email;
    @FXML
    HBox iban;
    @FXML
    HBox bic;
    @FXML
    Button updateButton;
    @FXML
    Button deleteButton;
    @FXML
    Button addButton;
    long eventId;


    private Participant participant;

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
        updateUIWithNewLanguage();
        reset();
        if (event == null) {
            return;
        }
        this.participant = participant;
        title.setText("Edit Participant");
        deleteButton.setOpacity(1);
        deleteButton.disableProperty().set(false);
        ;
        updateButton.setOpacity(1);
        updateButton.disableProperty().set(false);
        addButton.setOpacity(0);
        addButton.disableProperty().set(true);
        Text participantName = (Text) name.getChildren().get(0);
        participantName.setText(participantName.getText() + " "
                + participant.getNickname() + "  →");
        TextField newParticipantName = (TextField) name.getChildren().get(1);
        newParticipantName.setPromptText("Enter new Name");

        Text participantEmail = (Text) email.getChildren().get(0);
        String emailstring = participant.getEmail();
        if (emailstring.equals(""))
            emailstring = "-";
        participantEmail.setText(participantEmail.getText() + " "
                + emailstring + "  →");
        TextField newParticipantEmail = (TextField) email.getChildren().get(1);
        newParticipantEmail.setPromptText("Enter new Email");

        Text participantIban = (Text) iban.getChildren().get(0);
        String ibanString = participant.getIban();
        if (ibanString.equals(""))
            ibanString = "-";
        participantIban.setText(participantIban.getText() + " "
                + ibanString + "  →");
        TextField newParticipantIban = (TextField) iban.getChildren().get(1);
        newParticipantIban.setPromptText("Enter new IBAN");

        Text participantBIC = (Text) bic.getChildren().get(0);
        String bicString = participant.getBic();
        if (bicString.equals(""))
            bicString = "-";
        participantBIC.setText(participantBIC.getText() + " "
                + bicString + "  →");
        TextField newParticipantBic = (TextField) bic.getChildren().get(1);
        newParticipantBic.setPromptText("Enter new BIC");
    }

    /**
     * updates the participant.
     */
    public void updateParticipant() {
        String newNickname = titleField.getText();
        if (!newNickname.equals("")) {
            List<String> nicknames = server.getParticipants(eventId).stream()
                    .map(x -> x.getNickname()).toList();
            if (nicknames.contains(newNickname) && !participant.getNickname().equals(newNickname)) {
                showAlert(AlertType.ERROR, "Error", "There is already" +
                                " a participant with this name in this event",
                        "Please enter another name.");
                return;
            }
            participant.setNickname(newNickname);
        }


        String newEmail = titleField.getText();
        if (!newEmail.equals(""))
            participant.setEmail(newEmail);

        String newBic = titleField.getText();
        if (!newBic.equals(""))
            participant.setBic(newBic);

        String newIban = titleField.getText();
        if (!newIban.equals(""))
            participant.setIban(newIban);

        server.updateParticipant(eventId, participant);
        System.out.println("updated participant to " + participant);
        mainCtrl.goToOverview();
    }

    /**
     * deletes the participant.
     */
    public void deleteParticipant() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Participant");
        alert.setHeaderText("Are you sure you want to delete this participant?");
        alert.setContentText("This action cannot be undone. If this participant is a payer" +
                " in an expense, this expense will be removed. " +
                "It will be removed as ower if it was one.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Delete the participant from the server
                List<Expense> expensesWithParticipantAsPayer =
                        server.getExpensesByEventId(eventId).stream()
                                .filter(x -> x.getPayer().equals(participant)).toList();
                for (Expense expenseToDelete : expensesWithParticipantAsPayer) {
                    server.deleteExpense(eventId, expenseToDelete);
                }
                List<Expense> expensesWithParticipantAsOwer =
                        server.getExpensesByEventId(eventId).stream()
                                .filter(x -> x.getOwers().contains(participant)).toList();
                for (Expense expenseToUpdate : expensesWithParticipantAsOwer) {
                    expenseToUpdate.getOwers().remove(participant);
                    if (expenseToUpdate.getOwers().size() == 0) {
                        server.deleteExpense(eventId, expenseToUpdate);
                    } else {
                        server.updateExpense(eventId, expenseToUpdate);
                    }
                }

                server.deleteParticipant(eventId, participant);


                // Show confirmation message
                Alert deleteConfirmation = new Alert(Alert.AlertType.INFORMATION);
                deleteConfirmation.setTitle("Participant Deleted");
                deleteConfirmation.setHeaderText(null);
                deleteConfirmation.setContentText("Participant deleted successfully!");
                deleteConfirmation.showAndWait();

                mainCtrl.goToOverview();
            }
        });
    }

    /**
     * resets to values of creating a participant
     */
    public void reset() {
        title.setText(MainCtrl.resourceBundle.getString("Text.addExpense"));
        deleteButton.setOpacity(0);
        deleteButton.disableProperty().set(true);
        updateButton.setOpacity(0);
        updateButton.disableProperty().set(true);
        addButton.setOpacity(1);
        addButton.disableProperty().set(false);


        Text createName = (Text) name.getChildren().get(0);
        createName.setText("Name: ");
        TextField createNameTextField = (TextField) name.getChildren().get(1);
        createNameTextField.setPromptText(MainCtrl.resourceBundle.getString
                ("Text.enter") + " " + MainCtrl.resourceBundle.getString("Text.name"));
        createNameTextField.setText("");

        Text createEmail = (Text) email.getChildren().get(0);
        createEmail.setText("Email: ");
        TextField createEmailTextField = (TextField) email.getChildren().get(1);
        createEmailTextField.setPromptText
                (MainCtrl.resourceBundle.getString("Text.enter") + " Email");
        createEmailTextField.setText("");

        Text createIBAN = (Text) iban.getChildren().get(0);
        createIBAN.setText("IBAN: ");
        TextField createIbanTextField = (TextField) iban.getChildren().get(1);
        createIbanTextField.setPromptText
                (MainCtrl.resourceBundle.getString("Text.enter") + " IBAN");
        createIbanTextField.setText("");

        Text createBIC = (Text) bic.getChildren().get(0);
        createBIC.setText("BIC: ");
        TextField createBICTextField = (TextField) bic.getChildren().get(1);
        createBICTextField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " BIC");
        createBICTextField.setText("");
        updateUIWithNewLanguage();
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
                ok();
            }
        });
    }

    /**
     * stop filling in fields
     */

    public void ok() {
        String name = titleField.getText();
        if (server.getParticipants(eventId).stream()
                .map(x -> x.getNickname()).toList().contains(name)) {
            showAlert(AlertType.ERROR, "Error", "There is already" +
                    " a participant with this name in this event", "Please enter another name.");
            return;
        }
        String email = titleField.getText();
        String iban = titleField.getText();
        String bic = titleField.getText();

        if (!name.isEmpty()) {
            Participant participant = new Participant(name, email, bic, iban, 0.0);
            server.addParticipant(OverviewCtrl.getSelectedEvent().getId(), participant);

            mainCtrl.goToOverview();
        } else {
            showAlert(AlertType.ERROR, "Error", "Name field is empty", "Please enter a name.");
        }
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
        title.setText(MainCtrl.resourceBundle.getString("Text.addParticipant"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        addButton.setText(MainCtrl.resourceBundle.getString("button.add"));
        titleField.setText(MainCtrl.resourceBundle.getString("Text.name") + ": ");
        titleField.setText("Email:");
        titleField.setText("IBAN:");
        titleField.setText("BIC:");
        titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter")
                + " " + MainCtrl.resourceBundle.getString("Text.name"));
        titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " Email");
                titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " IBAN");
                titleField.setPromptText(MainCtrl.resourceBundle.getString("Text.enter") + " BIC");
        deleteButton.setText(MainCtrl.resourceBundle.getString("Text.delete"));
        updateButton.setText(MainCtrl.resourceBundle.getString("button.update"));
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
