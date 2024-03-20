package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
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
    @FXML
    private ArrayList<Label> labels;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Event selectedEvent;
    @FXML
    private Label eventName;
    @FXML
    private Label eventLocation;
    @FXML
    private Label eventDate;

    @FXML
    private TextField eventNameTextField;
    @FXML
    private TextField eventDateTextField;
    @FXML
    private TextField eventLocationTextField;
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
     * @param selectedEvent displays the info from the event selected from the table
     */
    public void displayEvent(Event selectedEvent) {
        eventName.setText(selectedEvent.getTitle());
        eventLocation.setText(selectedEvent.getLocation());
        eventDate.setText(String.valueOf(selectedEvent.getDate()));
        this.selectedEvent = selectedEvent;

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
     *
     */
    public void goToContact() {
        mainCtrl.goToContact();
    }

    /**
     *
     */
    public void sendInvites() {
        mainCtrl.sendInvites(eventName);
    }

    /* public HBox getHbox() {
         return hbox;
     }*/
    ///tried to create an hbox but it is not done
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refresh();
    }

    /**
     * refreshing!
     */
    public void refresh() {
        if (selectedEvent != null) {
            eventName.setText(selectedEvent.getTitle());
            eventLocation.setText(selectedEvent.getLocation());
            eventDate.setText(String.valueOf(selectedEvent.getDate()));
        }
        if (names != null && !names.isEmpty()) {
            myChoiceBox.getItems().add(names.get(names.size() - 1));
        }
        myChoiceBox.setOnAction(this::getName);
        hbox.setSpacing(5);
        labels = new ArrayList<>();
        if (names != null && !names.isEmpty()) {
            hbox.getChildren().addAll(new Label(names.get(names.size() - 1)));
        }
    }


    private void getName(javafx.event.ActionEvent actionEvent) {
        String name = myChoiceBox.getValue();
        myLabel.setText("From " + name);
        myLabel2.setText("Including " + name);
    }


    public void deleteEvent(ActionEvent actionEvent) {
    }

    public void goToAreYouSure(ActionEvent actionEvent) {
    }






    public void switchToNameTextField() {
        eventNameTextField.setVisible(true);
        eventNameTextField.requestFocus(); // Set focus to the text field
        eventName.setVisible(false);
    }

    public void switchToNameLabel() {
        eventNameTextField.setVisible(false);
        eventName.setText(eventNameTextField.getText());
        eventName.setVisible(true);
    }


    public void updateEventName(ActionEvent event) {
        switchToNameLabel();
    }

    public void switchToDateTextField() {
        eventDateTextField.setVisible(true);
        eventDateTextField.requestFocus(); // Set focus to the text field
        eventDate.setVisible(false);
    }

    public void switchToLocTextField() {
        eventLocationTextField.setVisible(true);
        eventLocationTextField.requestFocus(); // Set focus to the text field
        eventLocation.setVisible(false);
    }

    public void switchToDateLabel() {
        eventDateTextField.setVisible(false);

        eventDate.setText(eventDateTextField.getText());

        eventDate.setVisible(true);
    }

    public void updateEventDate(ActionEvent event) {
        switchToDateLabel();
    }
    public void switchToLocationLabel() {
        eventLocationTextField.setVisible(false);

        eventLocation.setText(eventLocationTextField.getText());

        eventLocation.setVisible(true);
    }
    public void updateEventLocation(ActionEvent event) {
        switchToLocationLabel();
    }

}
