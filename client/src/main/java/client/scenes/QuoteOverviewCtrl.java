package client.scenes;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

public class QuoteOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField eventName;
    private ObservableList<Event> data;

    @FXML
    private TableView<Event> table;
    @FXML
    private TableColumn<Event, String> colName;
    @FXML
    private TableColumn<Event, String> colLocation;
    @FXML
    private TableColumn<Event, String> colDate;

    /**
     * 
     * @param server
     * @param mainCtrl
     */
    @Inject
    public QuoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
    /**
     *
     * adds an event to the table

     */
    public void addEvent() {
        try {
            server.addEvent(getEvent());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverview();
    }
    private void clearFields() {
        eventName.clear();
    }


    /**
     *
     * @return return event
     */
    private Event getEvent() {
        return new Event(eventName.getText(), "empty description", "empty location", new Date());

    }

    /**
     * 
     */
    @SuppressWarnings("ParameterNumber")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(q ->
        new SimpleStringProperty(q.getValue().getTitle()));
        colLocation.setCellValueFactory(q ->
        new SimpleStringProperty(q.getValue().getLocation()));
        colDate.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getDescription()));
        table.setOnMouseClicked(this::handleTableItemClick);

    }

    /**
     * Event handler for clicking on a table item.
     */
    private void handleTableItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click
            Event selectedEvent = table.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                mainCtrl.showEventOverview(selectedEvent);
            }
        }
    }

    /**
     *
     */
    public void addQuote() {
        mainCtrl.showAdd();
    }

    /**
     * 
     */
    public void refresh() {
        var events = server.getEvents();
        data = FXCollections.observableList(events);
        table.setItems(data);
    }


    /**
     * 
     */
    public void page() {
        mainCtrl.showPage();
    }

    /**
     * 
     */


    /**
     * 
     */
    public void showAddExpenses() {
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
    public void goToOverview() {
        mainCtrl.goToOverview();
    }
}