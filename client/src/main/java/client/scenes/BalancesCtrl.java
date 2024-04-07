package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

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
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Text openDebtsText;
    @FXML
    private Text partialSettlersText;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private Menu currencyMenu;
    @FXML
    private Button addPartialButton;


    /**
     * constructor for the BalancesCtrl
     *
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
        colName.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getNickname()));

        colBalance.setCellValueFactory(q -> {
            double balance = q.getValue().getBalance() * Currency.getRate() / 100.0;
            return new SimpleStringProperty(String.valueOf(Currency.round(balance)) +
                    " " + Currency.getCurrencyUsed());
        });

        colSettles.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getPayer().getNickname() + " gave " +
                        q.getValue().getAmount() / 100 + " to " +
                        q.getValue().getOwers().get(0).getNickname()));

        colSettles.setCellFactory(tc -> {
            TableCell<Expense, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                    }
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (!cell.isEmpty() && event.getClickCount() == 2) {
                    Expense expense = cell.getTableView().getItems().get(cell.getIndex());
                    handleExpenseClick(expense);
                }
            });

            return cell;
        });
        addKeyboardNavigationHandlers();

    }

    private void handleExpenseClick(Expense expense) {
        mainCtrl.goToEditPartialDebt(event, expense);
    }

    /**
     * refresh function
     */
    public void refresh() {
        // Refresh the data for participants
        var participants = server.getParticipantsByEventId(event.getId());
        data = FXCollections.observableList(participants);
        table.setItems(data);

        // Refresh the data for settles
        var expenses = server.getExpensesByEventId(event.getId());
        var filteredExpenses = expenses.stream()
                .filter(x -> x.getTag().getName().equals("gifting money"))
                .toList();
        data2 = FXCollections.observableList(filteredExpenses);
        settles.setItems(data2);

        // Update currency for balances
        colBalance.setCellValueFactory(q -> {
            double balance = q.getValue().getBalance() * Currency.getRate() / 100;
            return new SimpleStringProperty(String.valueOf
                    (Currency.round(balance)) + " " + Currency.getCurrencyUsed());
        });
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
     *
     * @param event an Event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * back button
     */
    public void back() {
        mainCtrl.goToOverview();
    }


    /**
     * go to the settle debts page
     */
    public void settleDebts() {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(MainCtrl.resourceBundle.getString("Text.confirmation"));
        confirmationDialog.setHeaderText
                (MainCtrl.resourceBundle.getString("Text.areYouSureSettleDebts"));
        confirmationDialog.setContentText
                (MainCtrl.resourceBundle.getString("Text.warningSettleDebts"));

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                back();
                event.setClosed(true);
                event.setDate(new Date());
                server.updateEvent(event);
                letsSettle();
                mainCtrl.goToSettleDebts(event, server.getExpensesByEventId(event.getId()));
            } else {
                System.out.println(MainCtrl.resourceBundle.getString("Text.settleDebtsCancel"));
            }
        });
    }

    /**
     * lets settle
     */
    @SuppressWarnings("checkstyle:MethodLength")
    private void letsSettle() {
        Tag debt = new Tag("debt", null);
        debt.setEvent(event);
        debt = server.addTag(event.getId(), debt);
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
                .toList();
        int i = 0, j = 0;
        while (i < owe.size() && j < is_owed.size()) {
            Participant inDepted = owe.get(i);
            Participant deptor = is_owed.get(j);
            if (inDepted.getBalance() >= -deptor.getBalance()) {
                inDepted.setBalance(inDepted.getBalance()
                        + deptor.getBalance());
                Expense expense = new Expense();
                expense.setPayer(deptor);
                List<Participant> owed = new ArrayList<>();
                owed.add(inDepted);
                expense.setOwers(owed);
                expense.setAmount(-deptor.getBalance() / 100.0);
                expense.setDate(event.getDate());
                expense.setEvent(event);
                expense.setTitle("debts");
                expense.setTag(debt);
                server.addExpenseToEvent(event.getId(), expense);
                expenses.add(expense);

                //Update last activiy date of event
                event.setDate(new Date());
                server.updateEvent(event);

                j++;
                if (inDepted.getBalance() == 0)
                    i++;
            } else {
                deptor.setBalance(deptor.getBalance()
                        + inDepted.getBalance());
                Expense expense = new Expense();
                expense
                        .setPayer(deptor);
                List<Participant> owed = new ArrayList<>();
                owed.add(inDepted);
                expense.setOwers(owed);
                expense.setAmount(inDepted.getBalance() / 100.0);
                expense.setDate(event.getDate());
                expense.setEvent(event);
                expense.setTag(debt);
                expense.setTitle("debts");
                System.out.println(expense);
                server.addExpenseToEventDebt(event.getId(), expense);
                expenses.add(expense);

                //Update last activiy date of event
                event.setDate(new Date());
                server.updateEvent(event);

                i++;
            }
        }
    }

    /**
     * Changes the language of the site
     *
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
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
        updateUIWithNewLanguage();

    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {

        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.balances"));
        openDebtsText.setText(MainCtrl.resourceBundle.getString("Text.openDebts"));
        colName.setText(MainCtrl.resourceBundle.getString("Text.participant"));
        colBalance.setText(MainCtrl.resourceBundle.getString("Text.balance"));
        partialSettlersText.setText(MainCtrl.resourceBundle.getString("Text.partialSettlers"));
        settleDebtsButton.setText(MainCtrl.resourceBundle.getString("button.settleDebts"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
        addPartialButton.setText(MainCtrl.resourceBundle.getString("button.add"));
    }

    /**
     * Updates the flag image URL based on the selected language.
     *
     * @param language The selected language.
     */
    public void updateFlagImageURL(String language) {
        String flagImageUrl = ""; // Initialize with the default image URL
        switch (language) {
            case "english":
                flagImageUrl = "/client/scenes/images/BritishFlag.png";
                break;
            case "romana":
                flagImageUrl = "/client/scenes/images/RomanianFlag.png";
                break;
            case "nederlands":
                flagImageUrl = "/client/scenes/images/DutchFlag.png";
                break;
        }
        languageFlagImageView.setImage(new Image(getClass().getResourceAsStream(flagImageUrl)));
    }

    /**
     * changes the currency to whatever is selected
     *
     * @param event
     */
    @FXML
    public void changeCurrency(ActionEvent event) {
        RadioMenuItem selectedCurrencyItem = (RadioMenuItem) event.getSource();
        String currency = selectedCurrencyItem.getText();

        // Set the selected currency as the currency used for exchange rates
        Currency.setCurrencyUsed(currency.toUpperCase());

        // Print confirmation message

        System.out.println(MainCtrl.resourceBundle.getString
                ("Text.currencyChangedTo") + ": " + currency);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
        pause.setOnFinished(e -> refresh());
        pause.play();
        refresh();
    }


    /**
     * add partial debt
     */
    public void addPartial() {
        mainCtrl.goToPartial();
    }
}
