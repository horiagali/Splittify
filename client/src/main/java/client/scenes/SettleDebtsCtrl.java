package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Mail;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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
        mainCtrl.showOverview();
    }

    /**
     * setter for the event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }


    @SuppressWarnings("checkstyle:MethodLength")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        debtColumn.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getPayer().getNickname() + " gives " +
                        q.getValue().getAmount() + " to " +
                        q.getValue().getOwers().get(0).getNickname()));
        debtColumn.setCellFactory(column -> new TableCell<Expense, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Expense expense = getTableView().getItems().get(getIndex());
                    String bankInfo = "Bank information for payment:\n" +
                            "Account holder: " + expense.getOwers().get(0).getNickname() + "\n"+
                            "IBAN: " + expense.getOwers().get(0).getIban() + "\n" +
                            "BIC: " + expense.getOwers().get(0).getBic();
                    TitledPane titledPane = new TitledPane();
                    titledPane.setText(item);
                    Label bankInfoLabel = new Label(bankInfo);
                    VBox contentBox = new VBox(bankInfoLabel);
                    titledPane.setContent(contentBox);
                    titledPane.setMaxWidth(Double.MAX_VALUE);
                    setGraphic(titledPane);
                }
            }
        });
        reminderColumn.setCellFactory(col -> new TableCell<Expense, Void>() {
            private final Button reminderButton = new Button("Remind");

            // Initialization block for the TableCell instance
            {
                reminderButton.setOnAction(myevent -> {
                    Participant participant = getTableView().getItems().get(getIndex()).getPayer();
                    Participant owed = getTableView().getItems().get(getIndex()).getOwers().get(0);
                    double amount = getTableView().getItems().get(getIndex()).getAmount();
                    Mail mail = new Mail(participant.getEmail(), "Payment reminder " +
                            "for event " + event.getId().toString(), "You owe " +
                            owed.getNickname() + " " + String.valueOf(amount) + " for event "
                            + event.getTitle() + " on " + server.getServer());
                    server.sendEmail(mail);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(reminderButton);
                }
            }

        });
        tableView.getColumns().add(reminderColumn);
        actionColumn.setCellFactory(col -> new TableCell<Expense, Void>() {
            private final Button actionButton = new Button("Mark Received");
            {
                actionButton.setOnAction(myevent -> {
                    Expense currentExpense = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(currentExpense);
                    server.deleteExpenseDebt(event.getId(), currentExpense);
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
