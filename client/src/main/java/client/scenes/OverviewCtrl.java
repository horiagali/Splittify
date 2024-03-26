package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label myLabel;
    @FXML
    private Label myLabel2;
    @FXML
    private HBox hbox;

    private ArrayList<String> names;
    private ArrayList<Label> labels;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private static Event selectedEvent;
    private static Expense selectedExpense = null;

    @FXML
    private Label eventName;
    @FXML
    private Label eventLocation;
    @FXML
    private Label eventDate;

    @FXML
    private TextField eventNameTextField;
    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField eventLocationTextField;
    @FXML
    private ScrollPane participantsScrollPane;
    @FXML
    private VBox participantsVBox;
    @FXML
    private VBox expensesVBox;
    @FXML
    private ListView<Label> listExpenses;
    @FXML
    private Button editExpenseButton;
    @FXML
    private Button removeExpenseButton;


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
        if(!(selectedEvent.getDate() == null))
            eventDate.setText(selectedEvent.getDate().toString());
        setSelectedEvent(selectedEvent);

        displayExpenses();
    }


    /**
     * Functionality for remove expense button.
     */
    @FXML
    public void removeExpense() {

        try {
            Expense expense = selectedExpense;
            selectedEvent.getExpenses().remove(expense);
            server.deleteExpense(expense);
            selectedExpense = null;
            refresh();
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Select an expense!");
            alert.showAndWait();
        }
    }

    /**
     *
     */
    public void back() {
        mainCtrl.showOverview();
    }

    /**
     * @param name
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
     *  Goes to edit expense page upon press.
     */
   @FXML
    public void editExpense() {

        if (selectedExpense != null)
            mainCtrl.goToEditExpense(selectedEvent, selectedExpense);
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Select an expense!");
            alert.showAndWait();
        }
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
        displayExpenses();
        addKeyboardNavigationHandlers();
    }

    /**
     * refreshed the page, with the event data
     */
    public void refresh() {
        if (selectedEvent != null) {
            eventName.setText(selectedEvent.getTitle());
            eventLocation.setText(selectedEvent.getLocation());
            eventDate.setText(selectedEvent.getDate().toString());
            displayExpenses();
        }

//        hbox.setSpacing(5);    doesn't work, has to be fixed
        labels = new ArrayList<>();
        labels.addAll(names.stream().map(Label::new).toList());
//        hbox.getChildren().addAll(labels);
        loadParticipants();
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
                participantLabel.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        mainCtrl.goToEditParticipant(participant,selectedEvent);
                    }
                });
                participantsVBox.getChildren().add(participantLabel);
            }

            participantsScrollPane.setContent(participantsVBox);
        }
    }

    /**
     * Displays the expenses on the Overview page.
     */
    private void displayExpenses() {
        if (getSelectedEvent() != null) {
            List<Expense> expenses = server.getExpensesByEventId(getSelectedEvent().getId());
            
            listExpenses.getItems().clear();

            for (Expense expense : expenses) {

                Label expenseLabel = getLabel(expense);
                expenseLabel.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY)
                        setSelectedExpense(selectedEvent.getExpenses().stream()
                                .filter(x -> expenseLabel.getText().equals(x.getTitle()))
                                .toList().getFirst());
                });
                    //TODO: Add currency to label
                listExpenses.getItems().add(expenseLabel);
            }
        }
    }

    /**
     * Label generator for an expense.
     * @param expense the expense in question.
     * @return the new Label.
     */
    private static Label getLabel(Expense expense) {
        Label expenseLabel = new Label
                (expense.getTitle() + " - " + expense.getPayer() + " - " + expense.getAmount());
        expenseLabel.setTextFill(Color.BLACK);
        return expenseLabel;
    }

    private static void setSelectedExpense(Expense expense) {
        selectedExpense = expense;
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
//    private void getName(javafx.event.ActionEvent actionEvent) {
//        String name = myChoiceBox.getValue();
//        myLabel.setText("From " + name);
//        myLabel2.setText("Including " + name);
//    }

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
        eventDate.setText(String.valueOf(eventDatePicker.getValue()));
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
            eventDate.setText(String.valueOf(newDate));
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
        String location = eventLocation.getText();
        selectedEvent.setLocation(location);
        switchToLocationLabel();
        server.updateEvent(selectedEvent);

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
}
