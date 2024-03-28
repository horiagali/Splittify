package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BalancesCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private AnchorPane anchorPane;
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
    @FXML
    private Menu languageMenu;
    @FXML
    private ToggleGroup currencyGroup;


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
        addKeyboardNavigationHandlers();
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
                .filter(x -> x.getTag().getName().equals("gifting money"))
                .toList();
        data2 = FXCollections.observableList(filteredExpenses);
        settles.setItems(data2);
    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        anchorPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                refresh();
            }
        });
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
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Are you sure you want to settle the debts of the event?");
        confirmationDialog.setContentText("This action cannot be undone and will close the event");

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                back();
                letsSettle();
                mainCtrl.goToSettleDebts(event, expenses);
            } else {
                System.out.println("Settling of debts canceled.");
            }
        });
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

    /**
     * Changes the language of the site
     * @param event
     */
    @FXML
    public void changeLanguage(javafx.event.ActionEvent event) {
        RadioMenuItem selectedLanguageItem = (RadioMenuItem) event.getSource();
        String language = selectedLanguageItem.getText().toLowerCase();

        // Load the appropriate resource bundle based on the selected language
        MainCtrl.resourceBundle = ResourceBundle.getBundle("messages_" 
        + language, new Locale(language));
        
        Main.config.setLanguage(language);

        // Update UI elements with the new resource bundle
        updateUIWithNewLanguage();
    }
    
    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
    }

    /**
     * changes the currency to whatever is selected
     * @param event
     */
    @FXML
    public void changeCurrency(ActionEvent event) {
        RadioMenuItem selectedCurrencyItem = (RadioMenuItem) event.getSource();
        String currency = selectedCurrencyItem.getText();

        // Set the selected currency as the currency used for exchange rates
        Currency.setCurrencyUsed(currency.toUpperCase());

        // Print confirmation message
        System.out.println("Currency changed to: " + currency);
    }

}
