package client.scenes;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.UndoManager;
import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class OverviewCtrl implements Initializable {

    private ArrayList<String> names;
    private ArrayList<Label> labels;
    private List<Participant> participants;
    private List<Expense> expenses;
    private List<Tag> tags;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private static Event selectedEvent;
    private static boolean isAdmin;
    private static boolean isActive;
    private UndoManager undoManager = UndoManager.getInstance();

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label myLabel;
    @FXML
    private Label myLabel2;
    @FXML
    private Menu languageMenu;
    @FXML
    private Button balances;

    @FXML
    private Button giftMoney;
    @FXML
    private ChoiceBox<String> myChoiceBox;
    @FXML
    private HBox hbox;
    @FXML
    private ImageView languageFlagImageView;

    @FXML
    private Label eventLocation;
    @FXML
    private Text eventCode;
    @FXML
    private Label eventDescription;
    @FXML
    private Button backButton;

    @FXML
    private TextField eventNameTextField;
    @FXML
    private ScrollPane participantsScrollPane;
    @FXML
    private VBox participantsVBox;
    @FXML
    private ToggleGroup currencyGroup;

    @FXML
    private VBox expensesBox;
    @FXML
    private ComboBox<String> payer;
    @FXML
    private ComboBox<String> ower;
    @FXML
    private ComboBox<String> tag;
    @FXML
    private Button sendInvitesButton;
    @FXML
    private Menu currencyMenu;
    @FXML
    private Button tagButton;
    @FXML
    private Button undoButton;
    @FXML
    private Text expensesText;
    @FXML
    private Text participantsText;
    @FXML
    private Button addParticipantsButton;
    @FXML
    private Button goToBalances;
    @FXML
    private Button refreshButton;
    @FXML
    private Button addExpenseButton;
    @FXML
    private Button statisticsButton;
    @FXML
    private Text filtersText;
    @FXML
    private Text payerText;
    @FXML
    private Text owerText;
    @FXML
    private Text tagText;
    @FXML
    private Button deleteEventButton;
    @FXML
    private Text EventName;
    @FXML
    private Button editButton;


    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.names = new ArrayList<>();
    }

    /**
     * @param selectedEvent displays an event
     */
    public void displayEvent(Event selectedEvent) {
        setSelectedEvent(selectedEvent);
        setIsActive(true);
        refresh();
    }

    /**
     * Setter for isAdmin;
     * @param value boolean value.
     */
    public static void setIsAdmin(boolean value) {
        isAdmin = value;
    }

    /**
     * Sets isActive field, should be true is this page is being viewed
     * @param bool
     */
    public static void setIsActive(boolean bool) {
        isActive = bool;
    }

    /**
     *
     */
    public void back() {
        participantsVBox.getChildren().clear();
        expensesBox.getChildren().clear();
        setIsActive(false);
        setSelectedEvent(null);

        if (isAdmin) {
            mainCtrl.goToAdminPage();
            return;
        }
        mainCtrl.showOverview();
    }

    /**
     * @param name name.
     */
    public void addName(String name) {
        names.add(name);
    }

    /**
     *
     */
    public void addExpense() {
        setIsActive(false);
        mainCtrl.showAddExpenses();
    }

    /**
     *
     */
    public void goToContact() {
        setIsActive(false);
        mainCtrl.goToContact();
    }

    /**
     * go to tag overview of specific event
     */
    public void goToTagOverview() {
        setIsActive(false);
        mainCtrl.goToTagOverview(selectedEvent);
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

        int payerIndex = Math.max(payer.getSelectionModel().getSelectedIndex(), 0);
        int owerIndex = Math.max(ower.getSelectionModel().getSelectedIndex(), 0);
        int tagIndex = Math.max(tag.getSelectionModel().getSelectedIndex(), 0);
        payer.getSelectionModel().select(payerIndex);
        ower.getSelectionModel().select(owerIndex);
        tag.getSelectionModel().select(tagIndex);
    }


    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {

        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.overview"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        sendInvitesButton.setText(MainCtrl.resourceBundle.getString("button.sendInvites"));
        tagButton.setText(MainCtrl.resourceBundle.getString("button.tag"));
        expensesText.setText(MainCtrl.resourceBundle.getString("Text.expenses"));
        participantsText.setText(MainCtrl.resourceBundle.getString("Text.participants"));
        addParticipantsButton.setText(MainCtrl.resourceBundle.getString("button.add"));
        balances.setText(MainCtrl.resourceBundle.getString("button.balances"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        addExpenseButton.setText(MainCtrl.resourceBundle.getString("button.addExpense"));
        statisticsButton.setText(MainCtrl.resourceBundle.getString("button.seeStatistics"));
        filtersText.setText(MainCtrl.resourceBundle.getString("Text.filters"));
        payerText.setText(MainCtrl.resourceBundle.getString("Text.payer"));
        owerText.setText(MainCtrl.resourceBundle.getString("Text.ower"));
        tagText.setText(MainCtrl.resourceBundle.getString("Text.tag"));
        deleteEventButton.setText(MainCtrl.resourceBundle.getString("button.deleteEvent"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
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

        refresh();

        // Print confirmation message
        System.out.println(MainCtrl.resourceBundle.getString("Text.currencyChangedTo") + currency);
    }

    /**
     * goes to edit event info scene
     */
    public void editEvent() {
        setIsActive(false);
        mainCtrl.showEditEvent(selectedEvent);
    }

    /**
     *
     */
    public void sendInvites() {
        setIsActive(false);
        mainCtrl.sendInvites(EventName, selectedEvent);
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        refresh();
        loadParticipants();
        addKeyboardNavigationHandlers();
        payer.setValue("anyone");
        ower.setValue("anyone");
        tag.setValue("any tag");
        tag.setCellFactory(param -> createStringListCell());
        tag.setButtonCell(createStringListCell());
        ower.setCellFactory(param -> createStringListCell());
        ower.setButtonCell(createStringListCell());
        payer.setCellFactory(param -> createStringListCell());
        payer.setButtonCell(createStringListCell());
        isAdmin = false;

        server.registerForEvents("/topic/events", e -> handleDataPropagation(e));
        if (selectedEvent != null && selectedEvent.isClosed()){
            addParticipantsButton.setDisable(true);
            undoButton.setDisable(true);
            giftMoney.setDisable(true);
            balances.setDisable(true);
            addExpenseButton.setDisable(true);
            tagButton.setDisable(true);
            sendInvitesButton.setDisable(true);
        }else {
            addParticipantsButton.setDisable(false);
            giftMoney.setDisable(false);
            balances.setDisable(false);
            addExpenseButton.setDisable(false);
            undoButton.setDisable(false);
            tagButton.setDisable(false);
            sendInvitesButton.setDisable(false);
        }
        Image image = new Image(getClass()
        .getResourceAsStream("/client/scenes/images/edit_pencil_icon.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        editButton.setGraphic(imageView);
        editButton.setBackground(null);
    }

    private void handleDataPropagation(Long e) {
        if (selectedEvent != null && e.equals(selectedEvent.getId())) {
            Platform.runLater(() -> {
                refresh();
                loadUpdatedEventInfo();
            });
        }
    }

    /**
     * refreshed the page, with the event data
     */
    public void refresh() {
        if (selectedEvent != null && selectedEvent.isClosed()){
            addParticipantsButton.setDisable(true);
            giftMoney.setDisable(true);
            balances.setDisable(true);
            addExpenseButton.setDisable(true);
            undoButton.setDisable(true);
            tagButton.setDisable(true);
            sendInvitesButton.setDisable(true);

        }
        else {
            addParticipantsButton.setDisable(false);
            giftMoney.setDisable(false);
            balances.setDisable(false);
            addExpenseButton.setDisable(false);
            undoButton.setDisable(false);
            tagButton.setDisable(false);
            sendInvitesButton.setDisable(false);
        }
        if (selectedEvent != null) {
            loadUpdatedEventInfo();
            
        }

        int payerIndex = Math.max(payer.getSelectionModel().getSelectedIndex(), 0);
        int owerIndex = Math.max(ower.getSelectionModel().getSelectedIndex(), 0);
        int tagIndex = Math.max(tag.getSelectionModel().getSelectedIndex(), 0);

        if (MainCtrl.resourceBundle != null) {
            payer.setValue(MainCtrl.resourceBundle.getString("Text.anyone"));
            ower.setValue(MainCtrl.resourceBundle.getString("Text.anyone"));
            tag.setValue(MainCtrl.resourceBundle.getString("Text.anyTag"));
        }

        loadParticipants();
        loadComboBoxes();
        loadExpenses();
        labels = new ArrayList<>();
        labels.addAll(names.stream().map(Label::new).toList());

        payer.getSelectionModel().select(payerIndex);
        ower.getSelectionModel().select(owerIndex);
        tag.getSelectionModel().select(tagIndex);
    }

    private void loadUpdatedEventInfo() {
        selectedEvent = server.getEvent(selectedEvent.getId());
        loadEventInfo();
    }

    private void loadEventInfo() {
        EventName.setText(selectedEvent.getTitle());
        eventLocation.setText(selectedEvent.getLocation());
        eventCode.setText("The event code is: " + selectedEvent.getId());
        eventDescription.setText(selectedEvent.getDescription());
    }

    private void loadComboBoxes() {
        if (selectedEvent == null) return;
        List<String> participants = new ArrayList<>();
        participants.add(MainCtrl.resourceBundle.getString("Text.anyone"));
        participants.addAll(server.getParticipants(selectedEvent.getId()).stream()
                .map(Participant::getNickname).toList());
        List<String> tags = new ArrayList<>();
        tags.add(MainCtrl.resourceBundle.getString("Text.anyTag"));
        tags.addAll(server.getTags(selectedEvent.getId()).stream()
        .map(Tag::getName).filter(x -> !x.equals("gifting money") && !x.equals("debt")).toList());
        
        payer.setItems(FXCollections.observableArrayList(participants));
        ower.setItems(FXCollections.observableArrayList(participants));
        tag.setItems(FXCollections.observableArrayList(tags));

        if (MainCtrl.resourceBundle != null) {
            if (payer.getSelectionModel().getSelectedIndex() < 1)
                payer.getSelectionModel().select(0);
            if (ower.getSelectionModel().getSelectedIndex() < 1)
                ower.getSelectionModel().select(0);
            if (tag.getSelectionModel().getSelectedIndex() < 1)
                tag.getSelectionModel().select(0);
        }
    }

    /**
     * Creates a ListCell for the ComboBox to display participant nicknames.
     *
     * @return The created ListCell.
     */
    private ListCell<String> createStringListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        };
    }

    /**
     * Loads participants into page from DB
     */
    private void loadParticipants() {
        if (getSelectedEvent() != null) {
            List<Participant> participants =
                    server.getParticipants(OverviewCtrl.getSelectedEvent().getId());

            participantsVBox.getChildren().clear();

            for (Participant participant : participants) {
                Label participantLabel = new Label(participant.getNickname());
                participantLabel.setTextFill(Color.BLACK);
                participantLabel.setFont(Font.font(20));
                participantLabel.setMinWidth(150);
                participantLabel.setMaxWidth(150);
                participantLabel.setAlignment(Pos.CENTER);
                participantLabel.setOnMouseEntered(event -> {
                    participantLabel.setFont(Font.font(22));
                });
                addHoverAnimation(participantLabel, 1.1);

                participantLabel.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                        mainCtrl.goToContact(participant);
                        expensesBox.getChildren().clear();
                    }
                });
                participantsVBox.getChildren().add(participantLabel);
            }

            participantsScrollPane.setContent(participantsVBox);
        }
    }

    /**
     * resets comboboxes to 'anyone' state
     */
    public void resetComboBoxes() {
        payer.setValue(MainCtrl.resourceBundle.getString("Text.anyone"));
        ower.setValue(MainCtrl.resourceBundle.getString("Text.anyone"));
        tag.setValue(MainCtrl.resourceBundle.getString("Text.anyTag"));
    }

    /**
     * loads all expenses into event overview
     */
    public void loadExpenses() {
        expensesBox.getChildren().clear();
        if (selectedEvent == null) return;
        List<Expense> expenses = server.getExpensesByEventId(selectedEvent.getId())
                .stream().filter(x ->
                        !"gifting money".equalsIgnoreCase(x.getTag().getName()) &&
                                !"debt".equalsIgnoreCase(x.getTag().getName())).toList();
        expenses = applyFilters(expenses);
        if (expenses.isEmpty()) {
            statisticsButton.setDisable(true);
            expensesBox.getChildren()
                    .add(new Text(MainCtrl.resourceBundle.getString("Text.noExpensesFiltered")));
            return;
        } else {
            statisticsButton.setDisable(false);
        }
        
        
        for (Expense expense : expenses) {
            VBox vbox = new VBox();
            vbox.setMinWidth(300);
            vbox.setMaxWidth(300);
            vbox.setStyle("-fx-border-width: 2; -fx-border-radius: 5; -fx-border-color: black");
            vbox.setAlignment(Pos.CENTER);
            vbox.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    goToEditExpense(expense);
                }
            });
            setUpRow1(expense, vbox);
            setUpRow2(expense, vbox);
            addHoverAnimation(vbox, 1.05);
            expensesBox.getChildren().add(vbox);
        }
    }

    private List<Expense> applyFilters(List<Expense> expenses) {
        String payerBox = payer.getValue();
        String owerBox = ower.getValue();
        String tagBox = tag.getValue();
        if (payerBox != null && payer.getSelectionModel().getSelectedIndex() > 0) {
            expenses = expenses.stream()
                    .filter(x -> x.getPayer().getNickname().equals(payerBox)).toList();
        }
        if (owerBox != null && ower.getSelectionModel().getSelectedIndex() > 0) {
            Participant owerOfExpense = server
                    .getParticipantByNickname(selectedEvent.getId(), owerBox);
            expenses = expenses.stream()
                    .filter(x -> x.getOwers().contains(owerOfExpense)).toList();
        }
        if (tagBox != null && tag.getSelectionModel().getSelectedIndex() > 0) {
            Optional<Tag> selectedTag = server.getTags(selectedEvent.getId())
                    .stream().filter(x -> x.getName().equals(tagBox)).findFirst();
            if (!selectedTag.isEmpty()) {
                Tag actualTag = selectedTag.get();
                expenses = expenses.stream().filter(x -> x.getTag().equals(actualTag))
                        .toList();
            } else {
                String noTag = MainCtrl.resourceBundle.getString("Text.noTagWithName");
                String wasFound = MainCtrl.resourceBundle.getString("Text.wasFound");
                System.out.println(noTag + tagBox + wasFound);
            }

        }
        return expenses;
    }

    private void setUpRow2(Expense expense, VBox vbox) {
        HBox row2 = new HBox();
        row2.setMinWidth(280);
        row2.setMaxWidth(280);
        row2.setSpacing(5);
        row2.setAlignment(Pos.CENTER);
        Label payer = new Label(expense.getPayer().getNickname());
        String payed = MainCtrl.resourceBundle.getString("Text.payed");
        String forString = MainCtrl.resourceBundle.getString("Text.for");
        Label text = new Label(
                payed +
                        Currency.round(expense.getAmount() *
                                Currency.getRate(expense.
                                        getDate().toInstant().
                                        atZone(ZoneId.systemDefault()).toLocalDate()))
                        + " " + Currency.getCurrencyUsed() + " " + forString);
        String owers = "";
        if (expense.getOwers().size() == server.getParticipants(selectedEvent.getId()).size())
            owers = MainCtrl.resourceBundle.getString("Text.everyone");
        else {
            List<String> nameList = expense.getOwers().stream()
                    .map(Participant::getNickname).toList();
            owers = nameList.getFirst();
            for (int i = 1; i < nameList.size(); i++) {
                owers = owers + ", " + nameList.get(i);
            }
        }
        Label owersNames = new Label(owers);
        row2.getChildren().add(payer);
        row2.getChildren().add(text);
        vbox.getChildren().add(row2);
        vbox.getChildren().add(owersNames);
    }

    private void setUpRow1(Expense expense, VBox vbox) {
        HBox row1 = new HBox();
        Label label = new Label(expense.getTitle());
        label.setFont(Font.font(20));
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(150);
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
        var dateInString = ft.format(expense.getDate());
        Label date = new Label(dateInString);

        date.setMaxWidth(80);
        Button tag = new Button(expense.getTag().getName());

        row1.setMinWidth(280);
        row1.setMaxWidth(280);
        row1.setSpacing(10);
        row1.setAlignment(Pos.CENTER_LEFT);


        tag.setStyle("-fx-background-color: " + expense.getTag().getColor());
        date.setAlignment(Pos.CENTER);
        row1.getChildren().add(label);
        row1.getChildren().add(date);
        row1.getChildren().add(tag);
        vbox.getChildren().add(row1);
    }


    /**
     * Add keyboard navigation
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity"})
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                addExpense();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                sendInvites();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.B) {
                goToBalance();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                ActionEvent dummyEvent = new ActionEvent();
                goToAreYouSure(dummyEvent);
            }
            if (event.isAltDown() && event.getCode() == KeyCode.E){
                promptForItemIndex();
            }
            if (event.isAltDown() && event.getCode() == KeyCode.P){
                promptForParticipantIndex();
            }
            handleAdditionalKeyEvents(event);
        });
    }

    /**
     * Add keyboard navigation to select an expense
     */
    private void promptForItemIndex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Select Item");
        dialog.setHeaderText("Enter number of the expense you want to select");
        dialog.setContentText("Index:");

        // Show the dialog and wait for user input
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(indexStr -> {
            try {
                int index = Integer.parseInt(indexStr)-1;
                if (index >= 0 && index < expenses.size()) {
                    goToEditExpense(expenses.get(index));
                } else {
                    showErrorDialog("Invalid Index",
                            "Index must be between 1 and " +
                            expenses.size());
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input",
                        "Please enter a valid number.");
            }
        });
    }

    /**
     * Add keyboard navigation to select a participant
     */
    private void promptForParticipantIndex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Select Participant");
        dialog.setHeaderText("Enter the number of the participant you want to select:");
        dialog.setContentText("Index:");

        // Show the dialog and wait for user input
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(indexStr -> {
            try {
                int index = Integer.parseInt(indexStr) - 1;
                if (index >= 0 && index < participants.size()) {
                    mainCtrl.goToContact(participants.get(index));
                } else {
                    showErrorDialog("Invalid Index",
                            "Index must be between 1 and " + participants.size());
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input",
                        "Please enter a valid number.");
            }
        });
    }

    /**
     * Shows error dialog
     * @param title title of pop up
     * @param message content
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Add keyboard navigation
     * @param event link to other method
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity"})
    private void handleAdditionalKeyEvents(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.L) {
            languageMenu.show();
        }
        if (event.isControlDown() && event.getCode() == KeyCode.R) {
            refresh();
        }
        if(event.isControlDown() && event.getCode() == KeyCode.P){
            goToContact();
        }
        if(event.isAltDown() && event.getCode() == KeyCode.S){
            showStatistics();
        }
        if(event.isAltDown() && event.getCode() == KeyCode.T){
            goToTagOverview();
        }
        handleMoreAdditionalKeyEvents(event);
    }

    /**
     * Add keyboard navigation
     * @param event The KeyEvent triggering the navigation
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity"})
    private void handleMoreAdditionalKeyEvents(KeyEvent event) {
        if(event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.P){
            payer.show();
        }
        if(event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.T){
            ower.show();
        }
        if(event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.O){
            tag.show();
        }
        if(event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.E){
            editEvent();
        }
    }

    /**
     * get name
     *
     * @param actionEvent the actionEvent
     */
    private void getName(javafx.event.ActionEvent actionEvent) {
        String name = myChoiceBox.getValue();
        myLabel.setText("From " + name);
        myLabel2.setText("Including " + name);
    }

    /**
     * Sets the selected event
     *
     * @param selectedEvent
     */
    public static void setSelectedEvent(Event selectedEvent) {
        OverviewCtrl.selectedEvent = selectedEvent;
    }

    /**
     * Get the selected event
     *
     * @return selected event
     */
    public static Event getSelectedEvent() {
        return OverviewCtrl.selectedEvent;
    }


    /**
     *
     */
    public void showStatistics() {
        setIsActive(false);
        mainCtrl.goToStatistics(selectedEvent);
    }

    /**
     * asks if you really want to delete
     *
     * @param actionEvent
     */
    public void goToAreYouSure(ActionEvent actionEvent) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(MainCtrl.resourceBundle.getString("Text.confirmation"));
        confirmationDialog.setHeaderText
                (MainCtrl.resourceBundle.getString("Text.areYouSureDeleteEvent"));
        confirmationDialog.setContentText(MainCtrl.resourceBundle.getString("Text.noUndone"));

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                // Prevents 404 errors
                Event deleted = selectedEvent;
                OverviewCtrl.setSelectedEvent(null);
                setIsActive(false);

                Main.config.removeId(deleted.getId());
                server.deleteEvent(deleted);


                back();
            } else {
                System.out.println(MainCtrl.resourceBundle.getString("Text.eventDeleteCanceled"));
            }
        });
    }

    /**
     * debts button
     */
    public void checkDebts(){
        if(selectedEvent == null) return;
        if (!selectedEvent.isClosed()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Event not closed");
            alert.setHeaderText("Debts are not settled");
            alert.setContentText("To settle the debts, go to Balances.");
            alert.showAndWait();
        } else{
            mainCtrl.goToSettleDebts(selectedEvent,
                    server.getExpensesByEventId(selectedEvent.getId()));
        }
    }
    /**
>>>>>>> 14a3bd9ac08ecd37c47ff3c7f975f96dfea4fd91
     * Goes to edit expense.
     *
     * @param selectedExpense the expense to edit
     */
    public void goToEditExpense(Expense selectedExpense) {
        setIsActive(false);
        if (selectedExpense == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Edit expense");
            alert.setHeaderText("Error loading page");
            alert.setContentText("Please choose an expense!");
            alert.showAndWait();
        }
        else if(selectedEvent == null) return; else if (selectedEvent.isClosed()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Edit expense");
            alert.setHeaderText("The event is closed");
            alert.setContentText("You cannot edit expenses!");
            alert.showAndWait();
        }
        else
            mainCtrl.goToEditExpense(selectedEvent, selectedExpense);
    }

    /**
     * c
     *
     * @param actionEvent
     */
    public void goToBalances(ActionEvent actionEvent) {
        setIsActive(false);
        mainCtrl.goToBalances(selectedEvent);
    }

    /**
     * For keyboard press
     */
    public void goToBalance() {
        setIsActive(false);
        mainCtrl.goToBalances(selectedEvent);
    }

    private void addHoverAnimation(Node node, double factor) {
        double startX = node.getScaleX();
        double startY = node.getScaleY();
        double startZ = node.getScaleZ();
        node.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.1), node);
            scaleTransition.setFromX(startX);
            scaleTransition.setFromY(startY);
            scaleTransition.setFromZ(startZ);
            scaleTransition.setToX(node.getScaleX() * factor);
            scaleTransition.setToY(node.getScaleY() * factor);
            scaleTransition.setToZ(node.getScaleZ() * factor);
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
     * Handles the undo action to restore the previous state of an expense.
     * This method attempts to restore the expense from
     * the undo stack and update the server.
     * Displays appropriate alert messages based on the outcome.
     */
    @FXML
    public void undoAction() {
        UndoManager.ExpenseSnapshot previousExpenseState = undoManager.undo();

        if (previousExpenseState != null) {
            try {
                Long expenseIdToRestore = previousExpenseState.getExpenseId();
                List<Expense> expenses = server.getExpensesByEventId(selectedEvent.getId())
                        .stream()
                        .filter(expense -> !"gifting money".
                                equalsIgnoreCase(expense.getTag().getName()) && !"debt".
                                equalsIgnoreCase(expense.getTag().getName()))
                        .collect(Collectors.toList());

                Expense toUndo = expenses.stream()
                        .filter(expense -> Objects.equals(expense.getId(), expenseIdToRestore))
                        .findFirst()
                        .orElse(null);

                previousExpenseState.restore(toUndo);

                if (toUndo != null) {
                    server.updateExpense(selectedEvent.getId(), toUndo);
                    loadExpenses();
                    showAlert(Alert.AlertType.INFORMATION, "Expense Undo Successful",
                            "Restored Expense: " + toUndo);

                    // Update last change date
                    selectedEvent.setDate(new Date());
                    server.updateEvent(selectedEvent);

                } else {
                    showAlert(Alert.AlertType.WARNING, "Expense Not Found",
                            "Unable to find Expense with ID: "
                                    + expenseIdToRestore
                                    + " in the expenses list for the selected event.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Error while performing undo operation: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Undo Failed",
                    "Undo operation failed. " +
                            "No previous expense state found in the undo stack.");
        }
    }

    /**
     * Helper method to display an alert dialog with the specified type, title, and message.
     *
     * @param alertType The type of the alert (INFORMATION, WARNING, ERROR, etc.).
     * @param title     The title of the alert dialog.
     * @param message   The message content of the alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
