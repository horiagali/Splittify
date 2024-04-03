package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Mail;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Locale;
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
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Menu currencyMenu;
    @FXML
    private Text openDebtsText;
    @FXML
    private Button statisticsButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;

    /**
     * constructor for settle debts ctrl
     *
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
    public void back() {
        mainCtrl.showOverview();
    }

    /**
     * setter for the event
     *
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        display();
    }

    /**
     * Logic for SettleDebts;
     */
    @SuppressWarnings("checkstyle:MethodLength")
    public void display() {
        debtColumn.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getPayer().getNickname() + " " +
                        MainCtrl.resourceBundle.getString("Text.gives") + " " +
                        q.getValue().getAmount() + " "
                        + MainCtrl.resourceBundle.getString("Text.to") + " " +
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
                    String bankInfo = MainCtrl.resourceBundle.getString("Text.bankInformation")
                            + "\n"
                            + MainCtrl.resourceBundle.getString("Text.accountHolder") + " "
                            + expense.getOwers().get(0).getNickname() + "\n" +
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
            private final Button reminderButton = new Button
                    (MainCtrl.resourceBundle.getString("button.remind"));

            // Initialization block for the TableCell instance
            {
                reminderButton.setOnAction(myevent -> {
                    Participant participant = getTableView().getItems().get(getIndex()).getPayer();
                    Participant owed = getTableView().getItems().get(getIndex()).getOwers().get(0);
                    double amount = getTableView().getItems().get(getIndex()).getAmount();
                    Mail mail = new Mail(participant.getEmail(),
                            MainCtrl.resourceBundle.getString("Text.reminderText")
                            + event.getId().toString(),
                            MainCtrl.resourceBundle.getString("Text.youOwe") +
                            owed.getNickname() + " " + amount + " " +
                                    MainCtrl.resourceBundle.getString("Text.forEvent") + " "
                            + event.getTitle() + " " +
                                    MainCtrl.resourceBundle.getString("Text.on")
                                    + " " + server.getServer());
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
            private final Button actionButton = new Button
                    (MainCtrl.resourceBundle.getString("button.markReceived"));

            {
                actionButton.setOnAction(myevent -> {
                    Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationDialog.setTitle("Confirmation");
                    confirmationDialog.setHeaderText("Are you sure you want" +
                            " to mark this payment as received?");
                    confirmationDialog.setContentText("This action cannot be undone");

                    confirmationDialog.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            back();
                            Expense currentExpense = getTableView().getItems().get(getIndex());
                            server.deleteExpenseDebt(event.getId(), currentExpense);
                            refresh();
                        } else {
                            System.out.println("Settling of debts canceled.");
                        }
                    });
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
        List<Expense> expenses2 = server.getExpensesByEventId(event.getId())
                .stream().filter(x -> x.getTag().getName().equals("debt")).toList();
        data = FXCollections.observableList(expenses2);
        tableView.setItems(data);
    }

    /**
     * setter
     *
     * @param expenses
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * go to statistics page
     */
    public void goToStatistics() {
        mainCtrl.goToStatistics(event);
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
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
        display();
    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        openDebtsText.setText(MainCtrl.resourceBundle.getString("Text.openDebts"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
        statisticsButton.setText(MainCtrl.resourceBundle.getString("button.seeStatistics"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
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
    }
}
