package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {

    private ArrayList<String> names;
    private ArrayList<Label> labels;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private static Event selectedEvent;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label myLabel;
    @FXML
    private Label myLabel2;

    @FXML
    private ChoiceBox<String> myChoiceBox;
    @FXML
    private HBox hbox;
    @FXML
    private ImageView languageFlagImageView;

    @FXML
    private Label eventName;
    @FXML
    private Label eventLocation;
    @FXML
    private Label eventDate;
    @FXML
    private Label eventDescription;
    @FXML
    private Button backButton;

    @FXML
    private TextField eventNameTextField;
    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField eventLocationTextField;
    @FXML 
    private TextField eventDescriptionTextField;
    @FXML
    private ScrollPane participantsScrollPane;
    @FXML
    private VBox participantsVBox;
    @FXML
    private ListView<Expense> expensesListView;
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
        eventName.setText(selectedEvent.getTitle());
        eventLocation.setText(selectedEvent.getLocation());
        eventDate.setText("");
        eventDescription.setText(selectedEvent.getDescription());
        
        if(!(selectedEvent.getDate() == null)){
            SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            String dateInString = ft.format(selectedEvent.getDate());
            eventDate.setText(dateInString);
        }
        setSelectedEvent(selectedEvent);
    }

    /**
     *
     */

    public void back() {
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
        mainCtrl.showAddExpenses();
    }

    /**
     *
     */
    public void goToContact() {
        mainCtrl.goToContact();
    }

    /**
     * go to tag overview of specific event
     */
    public void goToTagOverview() {
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
        updateUIWithNewLanguage();
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
    }



    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        sendInvitesButton.setText(MainCtrl.resourceBundle.getString("button.sendInvites"));
        tagButton.setText(MainCtrl.resourceBundle.getString("button.tag"));
        expensesText.setText(MainCtrl.resourceBundle.getString("Text.expenses"));
        participantsText.setText(MainCtrl.resourceBundle.getString("Text.participants"));
        addParticipantsButton.setText(MainCtrl.resourceBundle.getString("button.add"));
        goToBalances.setText(MainCtrl.resourceBundle.getString("button.balances"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        addExpenseButton.setText(MainCtrl.resourceBundle.getString("button.addExpense"));
        statisticsButton.setText(MainCtrl.resourceBundle.getString("button.seeStatistics"));
        filtersText.setText(MainCtrl.resourceBundle.getString("Text.filters"));
        payerText.setText(MainCtrl.resourceBundle.getString("Text.payer"));
        owerText.setText(MainCtrl.resourceBundle.getString("Text.ower"));
        tagText.setText(MainCtrl.resourceBundle.getString("Text.tag"));
        deleteEventButton.setText(MainCtrl.resourceBundle.getString("button.deleteEvent"));

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

        // Print confirmation message
        System.out.println("Currency changed to: " + currency);
    }

    /**
     *
     */
    public void sendInvites() {
        mainCtrl.sendInvites(eventName, selectedEvent);
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
        
    }

    /**
     * refreshed the page, with the event data
     */
    public void refresh() {
        if (selectedEvent != null) {
            eventName.setText(selectedEvent.getTitle());
            eventLocation.setText(selectedEvent.getLocation());
            SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            String dateInString = ft.format(selectedEvent.getDate());
            eventDate.setText(dateInString);
            eventDescription.setText(selectedEvent.getDescription());
            
        }
    
        loadParticipants();
        loadComboBoxes();
        loadExpenses();
        labels = new ArrayList<>();
        labels.addAll(names.stream().map(Label::new).toList());
    }

    private void loadComboBoxes() {
        if(selectedEvent == null) return;
        List<String> participants = new ArrayList<>();
        participants.add("anyone");
        participants.addAll(server.getParticipants(selectedEvent.getId()).stream()
        .map(x -> x.getNickname()).toList());
        List<String> tags = new ArrayList<>();
        tags.add("any tag");
        tags.addAll(server.getTags(selectedEvent.getId()).stream()
        .map(x -> x.getName()).filter(x -> !x.equals("gifting money")).toList());
        
        payer.setItems(FXCollections.observableArrayList(participants));
        ower.setItems(FXCollections.observableArrayList(participants));
        tag.setItems(FXCollections.observableArrayList(tags));
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
        payer.setValue("anyone");
        ower.setValue("anyone");
        tag.setValue("any tag");
    }

    /**
     * loads all expenses into event overview
     */
    public void loadExpenses() {
        expensesBox.getChildren().clear();
        if(selectedEvent == null) return;
        List<Expense> expenses = server.getExpensesByEventId(selectedEvent.getId());
        expenses = applyFilters(expenses);
        if(expenses.size() == 0) {
            expensesBox.getChildren()
            .add(new Text("There are no expenses matching your filters."));
            return;
        }
        for(Expense expense : expenses) {
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
        if(payerBox != null && !payerBox.equals("anyone")) {
            expenses = expenses.stream()
            .filter(x -> x.getPayer().getNickname().equals(payerBox)).toList();
        }
        if(owerBox != null && !owerBox.equals("anyone")) {
            Participant owerOfExpense = server
            .getParticipantByNickname(selectedEvent.getId(), owerBox);
            expenses = expenses.stream()
        .filter(x -> x.getOwers().contains(owerOfExpense)).toList();
        }
        if(tagBox != null && !tagBox.equals("any tag")) {
            Optional<Tag> selectedTag = server.getTags(selectedEvent.getId())
            .stream().filter(x -> x.getName().equals(tagBox)).findFirst();
            if(!selectedTag.isEmpty()) {
                Tag actualTag = selectedTag.get();
                expenses = expenses.stream().filter(x -> x.getTag().equals(actualTag))
                .toList();
            } else {
                System.out.println("No tag with name " + tagBox + " was found!");
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
        Label text = new Label("payed " + expense.getAmount() + " for");
        String owers = "";
        if(expense.getOwers().size() == server.getParticipants(selectedEvent.getId()).size())
        owers = "everyone";
        else {
            List<String> nameList = expense.getOwers().stream()
            .map(x -> x.getNickname()).toList();
            owers = nameList.get(0);
            for(int i = 1; i < nameList.size(); i++) {
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
        });
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
        mainCtrl.goToStatistics(selectedEvent);
    }

    /**
     * asks if you really want to delete
     *
     * @param actionEvent
     */
    public void goToAreYouSure(ActionEvent actionEvent) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Are you sure you want to delete the event?");
        confirmationDialog.setContentText("This action cannot be undone.");

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                server.deleteEvent(selectedEvent);
                back();
            } else {
                System.out.println("Event deletion canceled.");
            }
        });
    }

    /**
     * switches to text field for name
     */
    public void switchToNameTextField() {
        eventNameTextField.setVisible(true);
        eventNameTextField.requestFocus();
        eventName.setVisible(false);
    }

    /**
     * swithces back to label
     */

    public void switchToNameLabel() {
        eventNameTextField.setVisible(false);
        eventName.setVisible(true);
    }

    /**
     * updated the name in the db
     *
     * @param event
     */
    public void updateEventName(ActionEvent event) {
        String name = eventNameTextField.getText();
        selectedEvent.setTitle(name);
        server.updateEvent(selectedEvent);

        switchToNameLabel();
        refresh();

    }

    /**
     * switches to text field for the date
     */

    public void switchToDateTextField() {
        eventDatePicker.setVisible(true);
        eventDatePicker.requestFocus();
        eventDate.setVisible(false);
    }

    /**
     * switches to text field for the location
     */
    public void switchToLocTextField() {
        eventLocationTextField.setVisible(true);
        eventLocationTextField.requestFocus();
        eventLocation.setVisible(false);
    }

    /**
     * switches to label field for the date
     */

    public void switchToDateLabel() {
        eventDatePicker.setVisible(false);
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
        String dateInString = eventDatePicker.getValue().getDayOfMonth() + "/" +
        eventDatePicker.getValue().getMonthValue() + "/" +
        eventDatePicker.getValue().getYear();
        eventDate.setText(dateInString);
        eventDate.setVisible(true);
    }

    /**
     * updates the event s date in the db
     *
     * @param event
     */
    public void updateEventDate(ActionEvent event) {

        try {
            LocalDate newDate = eventDatePicker.getValue();
            Date date = Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            selectedEvent.setDate(date);
            server.updateEvent(selectedEvent);
            SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            String dateInString = ft.format(selectedEvent.getDate());
            eventDate.setText(dateInString);
            switchToDateLabel();
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
    }

    /**
     * switches to the location label
     */
    public void switchToLocationLabel() {
        eventLocationTextField.setVisible(false);
        eventLocation.setText(eventLocationTextField.getText());
        eventLocation.setVisible(true);
    }

    /**
     * updates the event s location in the db
     *
     * @param event
     */
    public void updateEventLocation(ActionEvent event) {
        String location = eventLocationTextField.getText();
        selectedEvent.setLocation(location);
        switchToLocationLabel();
        server.updateEvent(selectedEvent);

    }

    /**
     * switches to text field for description
     */
    public void switchToDescriptionTextField() {
        eventDescriptionTextField.setVisible(true);
        eventDescriptionTextField.requestFocus();
        eventDescription.setVisible(false);
    }

    /**
     * switchces back to label
     */

    public void switchToDescriptionLabel() {
        eventDescriptionTextField.setVisible(false);
        eventDescription.setVisible(true);
    }

    /**
     * updated the description in the db
     *
     * @param event
     */
    public void updateEventDescription(ActionEvent event) {
        String description = eventDescriptionTextField.getText();
        selectedEvent.setDescription(description);
        server.updateEvent(selectedEvent);

        switchToDescriptionLabel();
        refresh();

    }

    /**
     * Deletes an expense.
     */
    public void deleteExpense() {
        try {
            Expense expense = expensesListView.getSelectionModel().getSelectedItem();
            selectedEvent.getExpenses().remove(expense);
            server.deleteExpense(selectedEvent.getId(), expense);
            refresh();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete expense");
            alert.setHeaderText("Error deleting");
            alert.setContentText("Please choose an expense!");
            alert.showAndWait();
        }
    }

    /**
     * Goes to edit expense.
     * @param selectedExpense the expense to edit
     */
    public void goToEditExpense(Expense selectedExpense){
        if (selectedExpense == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Edit expense");
            alert.setHeaderText("Error loading page");
            alert.setContentText("Please choose an expense!");
            alert.showAndWait();
        } else
            mainCtrl.goToEditExpense(selectedEvent, selectedExpense);
    }

    /**
     * c
     *
     * @param actionEvent
     */
    public void goToBalances(ActionEvent actionEvent) {
        mainCtrl.goToBalances(selectedEvent);
    }

    /**
     * For keyboard press
     */
    public void goToBalance() {
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
            scaleTransition.setToX(node.getScaleX()*factor);
            scaleTransition.setToY(node.getScaleY()*factor);
            scaleTransition.setToZ(node.getScaleZ()*factor);
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
}
