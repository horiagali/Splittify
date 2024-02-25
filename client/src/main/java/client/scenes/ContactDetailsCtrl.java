package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.BankAccount;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class ContactDetailsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Participant participant;
    private BankAccount bankAccount;
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField ibanField;

    @FXML
    private TextField bicField;

    @Inject
    public ContactDetailsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void abort() {
        clear();
        mainCtrl.showOverview();
    }
    public void clear(){
        nameField.clear();
        emailField.clear();
        ibanField.clear();
        bicField.clear();
    }
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
