package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class StartPageCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField eventName;

    private ObservableList<Event> data;

    @FXML
    private TableView<Event> table;


    @Inject
    public StartPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }
    public void back() {
        mainCtrl.showOverview();
    }
    public void refresh() {
        var events = server.getEvents();
        data = FXCollections.observableList(events);
        table.setItems(data);
    }



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

    private Quote getEvent() {   /// couldn t get the http request to work with event entity
        return new Quote(new Person(eventName.getText(),"no location added"),"no date added");    /// temporary here should be all event attributes

    }

    private void clearFields() {
        eventName.clear();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                addEvent();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }
    public void cancel() {
        clearFields();
        mainCtrl.showOverview();
    }



}
