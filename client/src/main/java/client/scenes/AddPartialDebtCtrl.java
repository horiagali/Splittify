package client.scenes;

import client.Main;
import client.utils.Currency;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class AddPartialDebtCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private List<CheckBox> participantCheckboxes = new ArrayList<>();
    private List<Participant> selectedParticipants = new ArrayList<>();
    private List<Participant> allParticipants = new ArrayList<>();
    @FXML
    private TextField purposeTextField;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox participantsVBox;
    @FXML
    private TextField amountTextField;
    @FXML
    private ComboBox<String> currencyComboBox;
    @FXML
    private CheckBox equallyCheckbox;
    @FXML
    private ComboBox<Participant> payerComboBox;
    @FXML
    private ComboBox<Participant> giftComboBox;
    @FXML
    private Menu languageMenu;
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Button back;
    @FXML
    private ToggleGroup currencyGroup;
    @FXML
    private DatePicker datePicker;


    /**
     * Constructs an instance of AddExpensesCtrl.
     *
     * @param server    The ServerUtils instance.
     * @param mainCtrl  The MainCtrl instance.
     */
    @Inject
    public AddPartialDebtCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the controller.
     * From Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addKeyboardNavigationHandlers();
        currencyComboBox.setOnKeyPressed(this::handleCurrencySwitch);
        datePicker.setValue(null);

        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            loadParticipants();
        }

        // Populate currency ComboBox
        currencyComboBox.setItems(FXCollections.observableArrayList("EUR", "CHF", "RON", "USD"));
        currencyComboBox.getSelectionModel().select("EUR");
    }

    /**
     * Loads the participants for the selected event and populates the UI elements accordingly.
     * If no event is selected or no participants are found
     * for the selected event, an error dialog is displayed.
     */
    private void loadParticipants() {
        giftComboBox.getItems().clear();
        payerComboBox.getItems().clear();
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent == null) {
            showErrorDialog("No event selected.");
            return;
        }
        List<Participant> participants = server.getParticipants(selectedEvent.getId());
        if (participants == null || participants.isEmpty()) {
            showErrorDialog("No participants found for the selected event.");
            return;
        }
        allParticipants.addAll(participants);
        configurePayerComboBox(participants);
        configureGiftComboBox(participants);
    }


    /**
     * Creates a CheckBox for the given participant.
     *
     * @param participant The participant for whom the CheckBox is created.
     * @return The created CheckBox.
     */
    private CheckBox createParticipantCheckbox(Participant participant) {
        CheckBox participantCheckbox = new CheckBox(participant.getNickname());
        participantCheckbox.setPrefWidth(80);
        participantCheckbox.setStyle("-fx-padding: 0 0 0 5;");
        participantCheckbox.setOnAction(event ->
                handleParticipantCheckboxAction(participantCheckbox));
        return participantCheckbox;
    }

    /**
     * Configures the payer ComboBox with the provided list of participants.
     *
     * @param participants The list of participants to populate the ComboBox.
     */
    private void configurePayerComboBox(List<Participant> participants) {
        payerComboBox.setCellFactory(param -> createParticipantListCell());
        payerComboBox.setButtonCell(createParticipantListCell());
        payerComboBox.setItems(FXCollections.observableArrayList(participants));
    }
    /**
     * Configures the gift ComboBox with the provided list of participants.
     *
     * @param participants The list of participants to populate the ComboBox.
     */
    private void configureGiftComboBox(List<Participant> participants) {
        giftComboBox.setCellFactory(param -> createParticipantListCell());
        giftComboBox.setButtonCell(createParticipantListCell());
        giftComboBox.setItems(FXCollections.observableArrayList(participants));
    }

    /**
     * Creates a ListCell for the ComboBox to display participant nicknames.
     *
     * @return The created ListCell.
     */
    private ListCell<Participant> createParticipantListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Participant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNickname());
                }
            }
        };
    }

    /**
     * Refreshes
     */
    public void refreshParticipants() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent != null) {
            loadParticipants();
        }
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

    /**
     * Handles participant checkboxes
     * @param checkBox checkbox
     */
    @FXML
    private void handleParticipantCheckboxAction(CheckBox checkBox) {
        String participantName = checkBox.getText();
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        if (selectedEvent == null) {
            return;
        }
        List<Participant> participants = server.getParticipants(selectedEvent.getId());
        Participant selectedParticipant = participants.stream()
                .filter(participant -> participantName.equals(participant.getNickname()))
                .findFirst()
                .orElse(null);
        if (selectedParticipant != null) {
            if (checkBox.isSelected()) {
                selectedParticipants.add(selectedParticipant);
            } else {
                selectedParticipants.remove(selectedParticipant);
            }
        }
    }



    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                addExpense();
            }
        });
    }

    /**
     * Handles switching of currencies with just keyboard presses
     * @param event keyboard press
     */
    private void handleCurrencySwitch(KeyEvent event) {
        if (event.getCode() == KeyCode.DOWN) {
            currencyComboBox.getSelectionModel().selectNext();
        } else if (event.getCode() == KeyCode.UP) {
            currencyComboBox.getSelectionModel().selectPrevious();
        }
    }

    /**
     * Handles the action when the user adds an expense.
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    @FXML
    private void addExpense() {
        Event selectedEvent = OverviewCtrl.getSelectedEvent();
        String amountText = amountTextField.getText();

        double amount = parseAmount(amountText);
        if (selectedEvent == null || amount < 0 || validateAmount(amountText)) {
            return;
        }

        Participant payer = findPayer();
        if (payer == null) {
            return;
        }

        // Check if at least one participant is selected
        Participant gift = findGift();
        if (gift == null) {
            return;
        }

        Date date = getDate();
        if (date == null) {
            showErrorDialog("Please select a date.");
            return;
        }
        List<Tag> tags = server.getTags(selectedEvent.getId());
        tags = tags.stream()
                .filter(tag -> "gifting money".equalsIgnoreCase(tag.getName()))
                .collect(Collectors.toList());
        Tag selectedTag = tags.get(0);
        selectedParticipants = new ArrayList<>();
        selectedParticipants.add(gift);
        Expense expense = createExpense("Gifting money", amount,
                date, payer, selectedParticipants, selectedTag);

        saveExpense(selectedEvent, expense);
        clearFieldsAndShowOverview(selectedEvent);
    }

    /**
     * Validates the amount entered by the user.
     *
     * @param amountText The text representing the amount entered by the user.
     * @return {@code true} if the amount is valid, {@code false} otherwise.
     */
    private boolean validateAmount(String amountText) {
        if (amountText.isEmpty()) {
            showErrorDialog("Please enter the amount.");
            return true;
        }
        try {
            Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for the amount.");
            return true;
        }
        return false;
    }

    /**
     * Parses the amount entered by the user.
     *
     * @param amountText The text representing the amount entered by the user.
     * @return The parsed amount as a double value.
     */
    private double parseAmount(String amountText) {
        try {
            return Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid number for the amount.");
            return -1;
        }
    }

    /**
     * Finds the payer selected by the user.
     *
     * @return The selected payer participant, or {@code null} if not found.
     */
    private Participant findPayer() {
        Participant selectedPayer = payerComboBox.getValue();
        if (selectedPayer == null) {
            showErrorDialog("Please select a payer.");
            return null;
        }
        String payerName = selectedPayer.getNickname();
        Participant payer = allParticipants.stream()
                .filter(participant -> payerName.equals(participant.getNickname()))
                .findFirst()
                .orElse(null);
        if (payer == null) {
            showErrorDialog("Payer not found.");
        }
        return payer;
    }

    /**
     * Finds the gifted selected by the user.
     *
     * @return The selected gift participant, or {@code null} if not found.
     */
    private Participant findGift() {
        Participant selectedGift = giftComboBox.getValue();
        if (selectedGift == null) {
            showErrorDialog("Please select a participant.");
            return null;
        }
        String giftName = selectedGift.getNickname();
        Participant gift = allParticipants.stream()
                .filter(participant -> giftName.equals(participant.getNickname()))
                .findFirst()
                .orElse(null);
        if (gift == null) {
            showErrorDialog("Participant not found.");
        }
        return gift;
    }
    /**
     * Gets the date from the datePicker.
     * @return the date.
     */
    private Date getDate() {
        if (datePicker.getValue() != null) {
            return Date.from(datePicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }


    /**
     * Creates an expense object.
     *
     * @param title               The title of the expense.
     * @param amount              The amount of the expense.
     * @param date                THe date of the expense.
     * @param payer               The participant who paid the expense.
     * @param selectedParticipants The participants involved in the expense.
     * @param selectedTag         The tag associated with the expense.
     * @return The created expense object.
     */
    private Expense createExpense(String title, double amount, Date date, Participant payer,
                                  List<Participant> selectedParticipants, Tag selectedTag) {
        return new Expense(title, amount, date, payer, selectedParticipants, selectedTag);
    }

    /**
     * Saves the expense to the server and clears selected participants.
     *
     * @param selectedEvent The selected event.
     * @param expense       The expense to be saved.
     */
    private void saveExpense(Event selectedEvent, Expense expense) {
        System.out.println(expense);
        server.addExpenseToEvent(selectedEvent.getId(), expense);
        selectedParticipants.clear();
    }

    /**
     * Clears input fields and shows the event overview.
     *
     * @param selectedEvent The selected event.
     */
    private void clearFieldsAndShowOverview(Event selectedEvent) {
        refreshUI();
        mainCtrl.goToBalances(selectedEvent);
    }

    /**
     * Refreshes the UI after adding an expense.
     */
    private void refreshUI() {
        loadParticipants();
        amountTextField.clear();
        datePicker.setValue(null);
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
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.addPartialDebt"));
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
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
     * Navigates back to the overview screen.
     */
    public void back() {
        refreshUI();
        mainCtrl.goToBalances(OverviewCtrl.getSelectedEvent());
    }

    /**
     * Shows an error dialog with the given error message.
     * @param errorMessage The error message to display.
     */
    private void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
