package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
    private Label eventLocation;
    @FXML
    private Label eventDate;
    @FXML
    private ChoiceBox<String> myChoiceBox;
    @FXML
    private HBox hbox;

    private ArrayList<String> names;
    @FXML
    private ArrayList<Label> labels;
    @FXML
    private Label eventName;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Event selectedEvent;

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
        mainCtrl.sendInvites(eventName, selectedEvent);
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

    /**
     * get name
     * @param actionEvent the actionEvent
     */
    private void getName(javafx.event.ActionEvent actionEvent) {
        String name = myChoiceBox.getValue();
        myLabel.setText("From " + name);
        myLabel2.setText("Including " + name);
    }


}
