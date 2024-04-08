package client.scenes;

import client.Main;
import client.UndoManager;
import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.text.Text;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class EditExpenseCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private static Event event;
    public static Expense expense;
    private List<CheckBox> participantCheckboxes = new ArrayList<>();
    private List<Participant> selectedParticipants = new ArrayList<>();
    private List<Participant> allParticipants = new ArrayList<>();
    private UndoManager undoManager = UndoManager.getInstance();
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
    private ComboBox<Tag> tagComboBox;
    @FXML
    private Menu languageMenu;
    @FXML
    private ToggleGroup currencyGroup;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Text editExpenseText;
    @FXML
    private Text whoPaidText;
    @FXML
    private Text whatForText;
    @FXML
    private Text howMuchText;
    @FXML
    private Text howToSplitText;
    @FXML
    private Button editButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button deleteButton;


    /**
     * Constructs an instance of AddExpensesCtrl.
     *
     * @param server   The ServerUtils instance.
     * @param mainCtrl The MainCtrl instance.
     */
    @Inject
    public EditExpenseCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter for the event.
     *
     * @param selectedEvent the event.
     */
    public static void setEvent(Event selectedEvent) {
        event = selectedEvent;
    }

    /**
     * Setter for the expense.
     *
     * @param selectedExpense the expense.
     */
    public static void setExpense(Expense selectedExpense) {
        expense = selectedExpense;
    }


    /**
     * Initializes the controller.
     * From Initializable
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        display();
    }

    private void loadExpense() {
        selectedParticipants.clear();
        payerComboBox.getSelectionModel().select(expense.getPayer());
        purposeTextField.setText(expense.getTitle());
        tagComboBox.getSelectionModel().select(expense.getTag());
        amountTextField.setText(String.valueOf(expense.getAmount()));
        Date dateDate = expense.getDate();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
        String dateInString = ft.format(dateDate);
        LocalDate date = LocalDate.of(Integer.parseInt(dateInString.substring(0, 4)),
                Integer.parseInt(dateInString.substring(5, 7)),
                Integer.parseInt(dateInString.substring(8, 10)));
        datePicker.setValue(date);
        equallyCheckbox.setSelected(expense.getOwers().equals(event.getParticipants()));
        for (CheckBox checkBox : participantCheckboxes) {
            if (expense.getOwers().stream()
                    .map(Participant::getNickname).toList().contains(checkBox.getText())) {
                checkBox.setSelected(true);
                Participant part = server
                        .getParticipantByNickname(event.getId(), checkBox.getText());
                if (!selectedParticipants.contains(part))
                    selectedParticipants.add(part);
            }
        }
    }

    /**
     * Updates the page.
     */
    protected void display() {
        participantCheckboxes.clear();
        participantsVBox.getChildren().clear();
        addKeyboardNavigationHandlers();
        currencyComboBox.setOnKeyPressed(this::handleCurrencySwitch);
        datePicker.setValue(null);

        if (event != null) {
            loadParticipants();
            loadTags();
        }

        // Populate currency ComboBox
        currencyComboBox.setItems(FXCollections.observableArrayList("USD", "EUR", "GBP", "JPY"));
        currencyComboBox.getSelectionModel().select("EUR");

        if (expense != null)
            loadExpense();
    }

    /**
     * Loads the participants for the selected event and populates the UI elements accordingly.
     * If no event is selected or no participants are found
     * for the selected event, an error dialog is displayed.
     */
    private void loadParticipants() {
        participantsVBox.getChildren().clear();
        payerComboBox.getItems().clear();
        List<Participant> participants = server.getParticipants(event.getId());
        allParticipants.addAll(participants);
        populateParticipantCheckboxes(participants);
        configurePayerComboBox(participants);
        for (CheckBox checkBox : participantCheckboxes) {
            checkBox.setSelected(expense.getOwers().stream().map
                    (Participant::getNickname).toList().contains(checkBox.getText()));
            System.out.println(checkBox.focusedProperty());
        }
        System.out.println();
    }

    /**
     * Populates the participant checkboxes in the UI with the provided list of participants.
     *
     * @param participants The list of participants to be displayed as checkboxes.
     */
    private void populateParticipantCheckboxes(List<Participant> participants) {
        for (Participant participant : participants) {
            CheckBox participantCheckbox = createParticipantCheckbox(participant);
            participantsVBox.getChildren().add(participantCheckbox);
            participantCheckboxes.add(participantCheckbox);
        }
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
        loadParticipants();
        loadTags();
    }

    /**
     * Loads the tags associated with the selected event from the
     * server and populates the tagComboBox.
     * If no event is selected or no tags are found for
     * the selected event, the tagComboBox will remain empty.
     */
    private void loadTags() {
        List<Tag> tags = server.getTags(event.getId());
        if (tags != null && !tags.isEmpty()) {
            tags = tags.stream()
                    .filter(tag -> !"gifting money".equalsIgnoreCase(tag.getName()))
                    .collect(Collectors.toList());
            ObservableList<Tag> tagList = FXCollections.observableArrayList(tags);
            tagComboBox.setItems(tagList);
            tagComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Tag item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                        setStyle("-fx-background-color: " + item.getColor());
                    }
                }
            });
            tagComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Tag item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                        setStyle("-fx-background-color: " + item.getColor());
                    }
                }
            });
        }
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

    }

    /**
     * Updates the flag.
     * @param language the language of the flag.
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

        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.editExpense"));
        editExpenseText.setText(MainCtrl.resourceBundle.getString("Text.editExpense"));
        whoPaidText.setText(MainCtrl.resourceBundle.getString("Text.whoPaid"));
        whatForText.setText(MainCtrl.resourceBundle.getString("Text.whatFor"));
        howMuchText.setText(MainCtrl.resourceBundle.getString("Text.howMuch"));
        howToSplitText.setText(MainCtrl.resourceBundle.getString("Text.howToSplit"));
        payerComboBox.setPromptText(MainCtrl.resourceBundle.getString("Text.selectName"));
        purposeTextField.setPromptText(MainCtrl.resourceBundle.getString("Text.purpose"));
        tagComboBox.setPromptText(MainCtrl.resourceBundle.getString("Text.selectTag"));
        datePicker.setPromptText(MainCtrl.resourceBundle.getString("Text.selectDate"));
        amountTextField.setPromptText(MainCtrl.resourceBundle.getString("Text.enterAmount"));
        cancelButton.setText(MainCtrl.resourceBundle.getString("button.cancel"));
        editButton.setText(MainCtrl.resourceBundle.getString("button.add"));
        equallyCheckbox.setText(MainCtrl.resourceBundle.getString("button.equally"));
        deleteButton.setText(MainCtrl.resourceBundle.getString("Text.delete"));
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
        System.out.println("Currency changed to: " + currency);
    }

    /**
     * Handles participant checkboxes
     *
     * @param checkBox checkbox
     */
    @FXML
    private void handleParticipantCheckboxAction(CheckBox checkBox) {
        if (event == null) {
            return;
        }
        List<Participant> participants = server.getParticipants(event.getId());
        Participant selectedParticipant = participants.stream()
                .filter(participant -> checkBox.getText().equals(participant.getNickname()))
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
     * Selects all checkboxes if someone presses split equally
     */
    @FXML
    private void handleEquallyCheckbox() {
        if (!participantCheckboxes.isEmpty()) {
            boolean selected = equallyCheckbox.isSelected();
            selectedParticipants.clear();
            if (selected) {
                if (event != null) {
                    List<Participant> participants = server.getParticipants(event.getId());
                    selectedParticipants.addAll(participants);
                }
            }
            for (CheckBox checkbox : participantCheckboxes) {
                checkbox.setSelected(selected);
            }
        } else {
            showErrorDialog("There are no participants to split the cost between");
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
                editExpense();
            }
        });
    }

    /**
     * Handles switching of currencies with just keyboard presses
     *
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
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    @FXML
    private void editExpense() {
        undoManager.captureState(expense);

        String title = purposeTextField.getText();
        if (title.isEmpty()) {
            showErrorDialog("Please set a title");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountTextField.getText());
        } catch (Exception e) {
            showErrorDialog("Please enter a valid amount.");
            return;
        }

        if (payerComboBox.getSelectionModel().getSelectedItem() == null) {
            showErrorDialog("Please select a payer");
            return;
        }
        Participant payer = payerComboBox.getValue();


        Tag selectedTag = tagComboBox.getValue();
        if (selectedTag == null) {
            showErrorDialog("Please select a tag.");
            return;
        }

        if (selectedParticipants.isEmpty()) {
            showErrorDialog("Please select at least one participant to split the cost.");
            return;
        }

        Date date = null;
        try {
            date = Date.from(datePicker.getValue().atStartOfDay
                    (ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            showErrorDialog("Please select a valid date!");
            return;
        }
        setExpense(title, amount, date, payer, selectedParticipants, selectedTag);
        saveExpense();

        // Capture the state of each field
        clearFieldsAndShowOverview(event);

        //Updates most recent change in event
        event.setDate(new Date());}


    /**
     * Creates an expense object.
     *
     * @param title                The title of the expense.
     * @param amount               The amount of the expense.
     * @param date                 THe date of the expense.
     * @param payer                The participant who paid the expense.
     * @param selectedParticipants The participants involved in the expense.
     * @param selectedTag          The tag associated with the expense.
     */
    private void setExpense(String title, double amount, Date date, Participant payer,
                            List<Participant> selectedParticipants, Tag selectedTag) {
        expense.reverseSettleBalance();
        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setDate(date);
        expense.setPayer(payer);
        expense.setOwers(selectedParticipants);
        expense.setTag(selectedTag);
        expense.settleBalance();
    }

    @FXML
    private void deleteExpense() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Expense");
        alert.setHeaderText("Are you sure you want to delete this expense?");
        alert.setContentText("This action cannot be undone");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Delete the expense from the server
                server.deleteExpense(event.getId(), expense);

                event.setDate(new Date());
                server.updateEvent(event);

                // Show confirmation message
                Alert deleteConfirmation = new Alert(Alert.AlertType.INFORMATION);
                deleteConfirmation.setTitle("Expense Deleted");
                deleteConfirmation.setHeaderText(null);
                deleteConfirmation.setContentText("Expense deleted successfully!");
                deleteConfirmation.showAndWait();
                back();
            }
        });
    }

    /**
     * Saves the expense to the server and clears selected participants.
     */
    private void saveExpense() {
        server.updateExpense(event.getId(), expense);
        refreshParticipants();
        refreshUI();

        event.setDate(new Date());
        server.updateEvent(event);
    }

    /**
     * Clears input fields and shows the event overview.
     *
     * @param selectedEvent The selected event.
     */
    private void clearFieldsAndShowOverview(Event selectedEvent) {
        refreshUI();
        mainCtrl.showEventOverview(selectedEvent);
    }

    /**
     * Refreshes the UI after adding an expense.
     */
    private void refreshUI() {
        participantCheckboxes.clear();
        participantsVBox.getChildren().clear();
        purposeTextField.clear();
        amountTextField.clear();
        equallyCheckbox.setSelected(false);
        datePicker.setValue(null);
    }

    /**
     * Navigates back to the overview screen.
     */
    public void back() {
        refreshUI();
        selectedParticipants.clear();
        mainCtrl.goToOverview();
    }

    /**
     * Shows an error dialog with the given error message.
     *
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
