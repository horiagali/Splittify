package client.scenes;

import java.net.URL;
import java.util.*;

import com.google.inject.Inject;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StatisticsCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Event event;
    ObservableList<PieChart.Data> pieChartData;
    List<Expense> expenses;
    List<Tag> tags;
    private static boolean isActive;
    double totalAmount;

    @FXML
    private Text eventTotalAmount;

    @FXML
    private Menu languageMenu;
    @FXML
    private Menu currencyMenu;

    @FXML
    private ToggleGroup currencyGroup;
    @FXML
    private ImageView languageFlagImageView;

    @FXML
    private PieChart pieChart;

    @FXML
    private Button back;

    @FXML
    private Button refresh;
    @FXML
    private VBox vbox;
    @FXML
    private ToggleButton toggleViewButton;

    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public StatisticsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;

    }

    /**
     * refreshes the data
     * uses rounding to ensure percentages round to 100%
     */
    public void refresh() {
        totalAmount = 0; //reset total amount every refresh (else it becomes more and mroe)
        if (!(this.pieChartData == null))
            pieChartData.clear();
        var expenses = server.getExpensesByEventId(event.getId());
        var tags = server.getTags(event.getId());
        var showByTag = toggleViewButton.isSelected();
        pieChartData = FXCollections.observableArrayList();
        createData(expenses, tags, showByTag);
        pieChart.setData(pieChartData);
        eventTotalAmount.setText("" + totalAmount);
        pieChart.setTitle("Statistics of this event");
        updateUIWithNewLanguage();

        //giving the pie chart the correct colors.
        for(var data : pieChartData) {
            var tag = expenses.stream().map(x -> x.getTag())
            .filter(x -> data.getName().substring(0, data.getName().indexOf(':'))
            .equals(x.getName()))
            .findFirst();
            if(tag.isEmpty()) continue;

            data.getNode().setStyle("-fx-pie-color: " + tag.get().getColor());
        }
        addKeyboardNavigationHandlers();

    }

    /**
     * creates the data to put in the piechart
     *
     * @param expenses list of expenses of this event.
     * @param tags     list of tags of this event.
     * @param showByPayer boolean to know which piechart to show
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    private void createData(List<Expense> expenses, List<Tag> tags, boolean showByPayer) {
        if (!showByPayer) {
            // Clear existing data
            pieChartData.clear();
            totalAmount = 0;

            // Calculate total expenses per tag
            Map<Tag, Double> tagExpenses = new HashMap<>();
            for (Expense expense : expenses) {
                Tag expenseTag = expense.getTag();
                String tagName = expenseTag.getName();

                if (tagName.equals("gifting money") || tagName.equals("debt")) {
                    continue; // Skip this expense if the tag is excluded
                }
                if (!tagExpenses.containsKey(expenseTag)) {
                    tagExpenses.put(expenseTag, 0.0);
                }
                double currentAmount = tagExpenses.get(expenseTag);
                tagExpenses.put(expenseTag, currentAmount + expense.getAmount());
            }

            // Add data to pie chart
            for (Tag tag : tagExpenses.keySet()) {
                double amount = tagExpenses.get(tag);
                amount = Currency.round(amount * Currency.getRate());
                if (amount != 0) {
                    pieChartData.add(new PieChart.Data(tag.getName() + ": "
                            + amount + " " + Currency.getCurrencyUsed(), amount));
                    totalAmount += amount;
                }
            }

            // Set percentages for each tag
            for (PieChart.Data data : pieChartData) {
                long percentage = Math.round((data.getPieValue() / totalAmount) * 100);
                data.setName(data.getName() + " - " + percentage + "%");
            }

            // Update pie chart and total amount display
            pieChart.setData(pieChartData);
            eventTotalAmount.setText("" + totalAmount + " " + Currency.getCurrencyUsed());
            pieChart.setTitle("Expenses by Tag");
            toggleViewButton.setText("View Expenses per Payer");
        } else {
            // Clear existing data
            pieChartData.clear();
            totalAmount = 0;

            // Calculate total expenses per payer
            Map<String, Double> participantExpenses = new HashMap<>();
            for (Expense expense : expenses) {
                String participantName = expense.getPayer().getNickname();
                if (!participantExpenses.containsKey(participantName)) {
                    participantExpenses.put(participantName, 0.0);
                }
                double currentAmount = participantExpenses.get(participantName);
                participantExpenses.put(participantName, currentAmount + expense.getAmount());
            }

            // Add data to pie chart based on participant expenses
            for (Map.Entry<String, Double> entry : participantExpenses.entrySet()) {
                String participantName = entry.getKey();
                double amount = entry.getValue();
                amount = Currency.round(amount * Currency.getRate());
                if (amount != 0) {
                    pieChartData.add(new PieChart.Data(participantName + ": "
                            + amount + " " + Currency.getCurrencyUsed(), amount));
                    totalAmount += amount;
                }
            }

            // Set percentages for each participant
            for (PieChart.Data data : pieChartData) {
                long percentage = Math.round((data.getPieValue() / totalAmount) * 100);
                data.setName(data.getName() + " - " + percentage + "%");
            }

            // Update pie chart and total amount display
            pieChart.setData(pieChartData);
            eventTotalAmount.setText("" + totalAmount + " " + Currency.getCurrencyUsed());
            pieChart.setTitle("Expenses per Participant");
            toggleViewButton.setText("View Expenses by Tag");
        }

        // Update UI elements with new language settings
        updateUIWithNewLanguage();
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
        System.out.println(MainCtrl.resourceBundle.getString("Text.currencyChangedTo") + currency);
    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.statistics"));
        String piechartString = "Text.statisticsTitle";
        if (event != null && event.getTitle() != null) {
            pieChart.setTitle(MainCtrl.resourceBundle.getString(piechartString) + event.getTitle());
        } else {
            pieChart.setTitle(MainCtrl.resourceBundle.getString(piechartString));
        }
        eventTotalAmount.setText(MainCtrl.resourceBundle.getString("Text.statisticsTotal") +
                totalAmount + " " + Currency.getCurrencyUsed());
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        refresh.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        String stageTitleString = "title.statistics";

        if(event != null) {
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString)
                    + event.getTitle());
        } else {
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString));
        }
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
     * set the selected event to see statistics from.
     *
     * @param selectedEvent
     */
    public void setEvent(Event selectedEvent) {
        StatisticsCtrl.event = server.getEvent(selectedEvent.getId());
    }

    /**
     * back button
     */
    public void back() {
        pieChart.getData().clear();
        setIsActive(false);
        toggleViewButton.setSelected(false);
        if (!event.isClosed())
            mainCtrl.goToOverview();
        else
            mainCtrl.goToSettleDebts(event, server.getExpensesByEventId(event.getId()));

    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        if (vbox == null) {
            return;
        }
        vbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                back();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.L) {
                languageMenu.show();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.M) {
                currencyMenu.show();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                refresh();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    handlePropagation();
                });

            }
        }, 0, 1000);
    }

    private void handlePropagation() {
        if (isActive) {
            if (event != null && (tags == null || expenses == null)) {
                tags = server.getTags(OverviewCtrl.getSelectedEvent().getId());
                expenses = server.getExpensesByEventId(OverviewCtrl.getSelectedEvent().getId());
            }

            if (event != null) {
                List<Tag> newTags = server.
                        getTags(OverviewCtrl.getSelectedEvent().getId());
                List<Expense> newExpenses = server.
                        getExpensesByEventId(OverviewCtrl.getSelectedEvent().getId());

                if (!tags.equals(newTags) ||
                        !expenses.equals(newExpenses)) {
                    tags = newTags;
                    expenses = newExpenses;
                    refresh();
                }

            }
        }
    }

    /**
     * Sets the page to active, so propagation can begin
     * @param bool true if page is being viewed, false otherwise
     */
    public static void setIsActive(boolean bool) {
        isActive = bool;
    }

    @FXML
    private void toggleView(ActionEvent event) {
        refresh();
    }
}
