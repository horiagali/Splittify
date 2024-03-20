package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {
    @FXML
    private Label myLabel;
    @FXML
    private Label myLabel2;

    @FXML
    private ChoiceBox<String> myChoiceBox;
    @FXML
    private HBox hbox;

    private ArrayList<String> names;
    private ArrayList<Label> labels;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private static Event selectedEvent;

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

    /**
     *
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
     *
     * @param selectedEvent
     * displays an event
     */
    public void displayEvent(Event selectedEvent) {
        eventName.setText(selectedEvent.getTitle());
        eventLocation.setText(selectedEvent.getLocation());
        eventDate.setText(selectedEvent.getDate().toString());
        this.selectedEvent = selectedEvent;
    }

    /**
     *
     */

    public void back() {
        mainCtrl.showOverview();
    }

    /**
     *
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
     *
     */
    public void goToContact() {
        mainCtrl.goToContact();
    }

    /**
     *
     */
    public void sendInvites() {
        mainCtrl.sendInvites(eventName, selectedEvent);
    }

    /**
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refresh();
    }

    /**
     * refreshed the page, with the event data
     */

    public void refresh() {
        if (selectedEvent != null) {
            eventName.setText(selectedEvent.getTitle());
            eventLocation.setText(selectedEvent.getLocation());
            eventDate.setText(selectedEvent.getDate().toString());
        }
        myChoiceBox.getItems().addAll(names);
        myChoiceBox.setOnAction(this::getName);
        hbox.setSpacing(5);
        labels = new ArrayList<>();
        labels.addAll(names.stream().map(Label::new).toList());
        hbox.getChildren().addAll(labels);
    }

    /**
     * get name
     * @param actionEvent the actionEvent
     */
    private void getName(javafx.event.ActionEvent actionEvent) {
        String name = myChoiceBox.getValue();
        myLabel.setText("From " + name);
        myLabel2.setText("Including " + name);
    }

    /**
     * Sets the selected event
     * @param selectedEvent
     */
    public static void setSelectedEvent(Event selectedEvent){
        OverviewCtrl.selectedEvent = selectedEvent;
    }

    /**
     * Get the selected event
     * @return selected event
     */
    public static Event getSelectedEvent(){
        return OverviewCtrl.selectedEvent;
    }


    /**
     * asks if you really want to delete
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
        });    }

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
     * @param event
     */
    public void updateEventLocation(ActionEvent event) {
        String location = eventLocation.getText();
        selectedEvent.setLocation(location);
        switchToLocationLabel();
        server.updateEvent(selectedEvent);

    }
}
