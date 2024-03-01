package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class InviteCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private Label name;
    @FXML
    private Label code;

    /**
     * 
     * @param server
     * @param mainCtrl
     */
    @Inject
    public InviteCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     *
     * @param eventName
     */
    public void setName(Label eventName){
        ///temporary, we should get it from the database
        name.setText(eventName.getText());
        //name.setText("New Year Party");
        name.setStyle("-fx-font-weight: bold");
    }

    /**
     * 
     */
    public void setCode(){
        ///temporary, we should get it from the database
        code.setText("AC74ED");
        code.setStyle("-fx-font-weight: bold");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCode();
    }
}
