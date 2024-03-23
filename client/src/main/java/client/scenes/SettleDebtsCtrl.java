package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettleDebtsCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Event event;
    private List<Expense> expenses;
    @FXML
    private TableView<Expense> tableView;
    @FXML
    private TableColumn<Expense, Button> actionColumn;
    @FXML
    private TableColumn<Expense, String> debtColumn;
    @FXML
    private ObservableList<Expense> data;
    /**
     * constructor for settle debts ctrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SettleDebtsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * back to the balances page
     */
    public void back(){
        mainCtrl.goToBalances(event);
    }

    /**
     * setter for the event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            actionColumn.setCellFactory(col -> new TableCell<Expense, Button>() {
                @FXML
                private final Button actionButton = new Button("Mark Received");
            });
            debtColumn.setCellValueFactory(q ->
                    new SimpleStringProperty(q.getValue().getPayer().getNickname() + " gives " +
                            q.getValue().getAmount() + " to " +
                            q.getValue().getOwers().get(0).getNickname()));
    }
    /**
     * refresh function
     */
    public void refresh() {
        data = FXCollections.observableList(expenses);
        tableView.setItems(data);
    }

    /**
     * setter
     * @param expenses
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
