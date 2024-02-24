package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

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

    private String[] names = {"Iulia", "Martijn", "Horia", "Amanda", "Fayaz", "Mihnea"};
    @FXML
    private ArrayList<Label> labels;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void back() {
        mainCtrl.showOverview();
    }

    public void addExpense() {
        mainCtrl.showAddExpenses();
    }

    public void goToContact() {
        mainCtrl.goToContact();
    }

    public void sendInvites(){
        mainCtrl.sendInvites();
    }
    public HBox getHbox() {
        return hbox;
    }
    ///tried to create an hbox but it is not done
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myChoiceBox.getItems().addAll(names);
        myChoiceBox.setOnAction(this::getName);
        hbox = new HBox();
        labels = new ArrayList<>();
        for (String s: names){
            Label label = new Label(s);
            label.setText(s);
            label.setTextFill(Color.web("#000000"));
            labels.add(label);
        }
        hbox.getChildren().addAll(labels);
    }

    private void getName(javafx.event.ActionEvent actionEvent) {
        String name = myChoiceBox.getValue();
        myLabel.setText("From " + name);
        myLabel2.setText("Including " + name);
    }


}
