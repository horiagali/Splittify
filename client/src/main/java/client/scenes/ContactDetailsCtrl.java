package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.BankAccount;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;


public class ContactDetailsCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Participant participant;
    private BankAccount bankAccount;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ibanField;

    @FXML
    private TextField bicField;

    /**
     * 
     * @param server
     * @param mainCtrl
     */
    @Inject
    public ContactDetailsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

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
                abort();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.P) {
                ok();
            }
        });
    }

    /**
     * stop filling in fields
     */
    public void abort() {
        clear();
        mainCtrl.showOverview();
    }

    /**
     * clear fields
     */
    public void clear(){
        nameField.clear();
        emailField.clear();
        ibanField.clear();
        bicField.clear();
    }

    /**
     * 
     */
    public void ok() {
//        try {
//            server.addQuote(getQuote());
//        } catch (WebApplicationException e) {
//
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.initModality(Modality.APPLICATION_MODAL);
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//            return;
//        }
        mainCtrl.addToOverview(nameField.getText());
        clear();
        mainCtrl.goToOverview();
    }


}
