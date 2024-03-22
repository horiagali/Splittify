package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BalancesCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TableView<Participant> table;
    @FXML
    private TableColumn<Participant, String> colName;
    @FXML
    private TableColumn<Participant, String> colBalance;
    @FXML
    private TableView<Expense> settles;
    @FXML
    private TableColumn<Expense, String> colSettles;
    @FXML
    private ObservableList<Participant> data;
    @FXML
    private ObservableList<Expense> data2;
    private List<Expense> expenses;
    private Event event;


    /**
     * constructor for the BalancesCtrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public BalancesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //refresh();
        colName.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getNickname()));
        colBalance.setCellValueFactory(q ->
                new SimpleStringProperty(String.valueOf(q.getValue().getBalance())));
        colSettles.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getPayer().getNickname() + " gave " +
                        q.getValue().getAmount() + " to " +
                        q.getValue().getOwers().get(0).getNickname()));
    }

    /**
     * refresh function
     */
    public void refresh() {
        var participants = server.getParticipantsByEventId(event.getId());
        data = FXCollections.observableList(participants);
        table.setItems(data);
        var expenses = server.getExpensesByEventId(event.getId());
        var filteredExpenses = expenses.stream()
                .filter(x -> x.getTag().getName().equals("Gifting Money"))
                .toList();
        data2 = FXCollections.observableList(filteredExpenses);
        settles.setItems(data2);
    }
    /**
     * setter for the event
     * @param event an Event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * back button
     */
    public void back(){
        mainCtrl.goToOverview();
    }

    /**
     * go to the settle debts page
     */
    public void settleDebts(){
        letsSettle();
        //server.settleDebts(event);
        mainCtrl.goToSettleDebts(event, expenses);
    }

    /**
     * lets settle
     */
    private void letsSettle() {
        expenses = new ArrayList<>();
        List<Participant> participants = server.getParticipantsByEventId(event.getId());
        List<Participant> owe = participants.stream()
                .filter(x -> x.getBalance() > 0)
                .sorted((a, b) ->
                        (int) (a.getBalance()
                                - b.getBalance()))
                .toList();
        List<Participant> is_owed = participants.stream()
                .filter(x -> x.getBalance() < 0)
                .sorted((a, b) ->
                        (int) (a.getBalance()
                                - b.getBalance()))
                .toList();int i = 0, j = 0;
        while(i < owe.size() && j < is_owed.size()) {
            Participant inDepted = owe.get(i);
            Participant deptor = is_owed.get(j);
            if (inDepted.getBalance() >= -deptor.getBalance()) {
                inDepted.setBalance(inDepted.getBalance()
                        + deptor.getBalance());
                Expense expense = new Expense();
                expense.setPayer(deptor);
                List<Participant> owed = new ArrayList<>();
                owed.add(inDepted);expense.setOwers(owed);
                expense.setAmount(-deptor.getBalance());
                expenses.add(expense);j++;
                if (inDepted.getBalance() == 0)
                    i++;
            } else {
                deptor.setBalance(deptor.getBalance()
                        + inDepted.getBalance());
                Expense expense = new Expense();expense
                        .setPayer(deptor);
                List<Participant> owed = new ArrayList<>();
                owed.add(inDepted);expense.setOwers(owed);
                expense.setAmount(inDepted.getBalance());
                expenses.add(expense);i++;
            }
        }
    }

}
