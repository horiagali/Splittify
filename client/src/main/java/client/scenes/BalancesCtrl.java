package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class BalancesCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TableView<Participant> table;
    @FXML
    private TableColumn<Participant, String> colName;
    @FXML
    private TableColumn<Participant, String> colBalance;

    @FXML
    private ObservableList<Participant> data;
    private Event event;


    /**
     * constructor for the BalancesCtrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public BalancesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colName.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getNickname()));
        colBalance.setCellValueFactory(q ->
                new SimpleStringProperty(String.valueOf(q.getValue().getBalance())));
    }

    /**
     * refresh function
     */
    public void refresh() {
        var participants = server.getParticipantsByEventId(event.getId());
        data = FXCollections.observableList(participants);
        table.setItems(data);
    }
    /**
     * setter for the event
     * @param event an Event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * back button
     */
    public void back(){
        mainCtrl.goToOverview();
    }
}
