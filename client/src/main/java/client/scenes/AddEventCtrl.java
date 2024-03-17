package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.util.Date;

public class AddEventCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private Button addEventButton;
    @Inject
    public AddEventCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void addEvent(ActionEvent ae) {
        Event newEvent = new Event(
                nameField.getText(),
                descriptionField.getText(),
                locationField.getText(),
                new Date()
        );

        try {
            server.addEvent(newEvent);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        clearFields();
        mainCtrl.showOverview();
    }
    public void clearFields() {
        nameField.clear();
        descriptionField.clear();
        locationField.clear();
    }
}
