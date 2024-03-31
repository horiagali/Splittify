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
import javafx.scene.control.Button;
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
    private TableColumn<Expense, Void> actionColumn;
    @FXML
    private TableColumn<Expense, String> debtColumn;
    @FXML
    private TableColumn<Expense, Void> reminderColumn;
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
        debtColumn.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getPayer().getNickname() + " gives " +
                        q.getValue().getAmount() + " to " +
                        q.getValue().getOwers().get(0).getNickname()));
        actionColumn.setCellFactory(col -> new TableCell<Expense, Void>() {
            private final Button actionButton = new Button("Mark Received");

            {
                actionButton.setOnAction(event -> {
                    Expense currentExpense = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(currentExpense);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        });

        tableView.getColumns().add(actionColumn);
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
