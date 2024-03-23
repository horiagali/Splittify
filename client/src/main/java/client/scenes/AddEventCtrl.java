package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import java.time.LocalDate;
import java.net.URL;
import java.util.ResourceBundle;

public class AddEventCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private Button addEventButton;
    @FXML
    private Button cancelButton;

    /**
     * Constructor for the controller
     * @param server server
     * @param mainCtrl mainCtrl
     */
    @Inject
    public AddEventCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
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
        addKeyboardNavigationHandlers();
    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                ActionEvent dummyEvent = new ActionEvent();
                cancel(dummyEvent);
            }
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                ActionEvent dummyEvent = new ActionEvent();
                addEvent(dummyEvent);
            }
        });
    }

    /**
     * Adds an event directly into the database. No checking of values
     * @param ae action event
     */
    public void addEvent(ActionEvent ae) {
        if (nameField.getText().isEmpty() || nameField.getText().isBlank()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Please enter a title");
            alert.showAndWait();
            return;
        }

        Event newEvent = new Event(
                nameField.getText(),
                descriptionField.getText(),
                locationField.getText(),
                null // Some jackson error from local date
        );

        try {
            server.sendEvent("app/events", newEvent);
            //server.addEvent(newEvent);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Return user to QuoteOveview
     * @param e actionEvent
     */
    public void cancel(ActionEvent e) {
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Clears all text fields
     */
    public void clearFields() {
        nameField.clear();
        descriptionField.clear();
        locationField.clear();
    }
}
