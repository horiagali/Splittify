package client.scenes;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class StatisticsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Event event;
    ObservableList<PieChart.Data> pieChartData;
    double totalAmount;

    @FXML
    private Text eventTotalAmount;
    
    @FXML
    private Menu languageMenu;

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
    private Menu currencyMenu;

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
        if(!(this.pieChartData == null))
            pieChartData.clear();
        var expenses = server.getExpensesByEventId(event.getId());
        var tags = server.getTags(event.getId());
        pieChartData = FXCollections.observableArrayList();
        createData(expenses, tags);
        pieChart.setData(pieChartData);
        eventTotalAmount.setText("" + totalAmount);
        updateUIWithNewLanguage();

        //giving the pie chart the correct colors. Cant get the legend to have the correct colors,
        //so i also wont change to colors in the pie chart itself.
        // for(var data : pieChartData) {
        //     var tag = expenses.stream().map(x -> x.getTag())
        //     .filter(x -> data.getName().substring(0, data.getName().indexOf(':'))
        //     .equals(x.getName()))
        //     .findFirst();
        //     if(tag.isEmpty()) continue;

        //     data.getNode().setStyle("-fx-pie-color: " + tag.get().getColor());
        // }

    }

    /**
     * creates the data to put in the piechart
     * @param expenses list of expenses of this event.
     * @param tags list of tags of this event.
     */
    private void createData(List<Expense> expenses, List<Tag> tags) {
        //method to group all expenses on tag and get their total amount
        for(Tag tag : tags) {
            if(tag.getName().equals("gifting money") || tag.getName().equals("debt"))
                continue;
            double amount = expenses.stream().filter(x -> x.getTag().equals(tag))
            .mapToDouble(x  -> (int) x.getAmount()).sum();
            if(amount != 0)
                pieChartData.add(new PieChart.Data(tag.getName() + ": " + amount, amount));
            totalAmount += amount;
        }
        //method to set the percentage per tag group
        long remainingPercentage = 100;
        String lastName = "";
        for(PieChart.Data data : pieChartData) {
            long percentage = Math.round((data.getPieValue() / totalAmount)*100);
            lastName = data.getName();
            data.setName(data.getName() + " - " + percentage + "%");
            remainingPercentage = remainingPercentage - percentage;
        }
        //if percentage does not match due to rounding,
        //just add or substract the last 1 or 2 percent to the last tag
        if(remainingPercentage != 0 && pieChartData != null && pieChartData.size() > 0) {
            PieChart.Data dataToEdit = pieChartData.get(pieChartData.size()-1);
            long percentage = Math.round((dataToEdit.getPieValue() / totalAmount)*100);
            remainingPercentage = remainingPercentage + percentage;
            dataToEdit.setName(lastName + " - " + remainingPercentage + "%");
            remainingPercentage = 0;
            
        }
        pieChart.setData(pieChartData);
        eventTotalAmount.setText("" + totalAmount);
        updateUIWithNewLanguage();

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

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        String piechartString = "Text.statisticsTitle";
        if(event !=null){
            pieChart.setTitle(MainCtrl.resourceBundle.getString(piechartString) + event.getTitle());
        }
        else {
            pieChart.setTitle(MainCtrl.resourceBundle.getString(piechartString));
        }
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
        eventTotalAmount.setText(MainCtrl.resourceBundle.getString("Text.statisticsTotal") + 
        totalAmount);
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        refresh.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        String stageTitleString = "title.statistics";

        if(event != null) {
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString)
                    +  " " + event.getTitle());
        }
        else{
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
     * @param selectedEvent
     */
    public static void setEvent(Event selectedEvent) {
        StatisticsCtrl.event = selectedEvent;
    }

    /**
     * back button
     */
    public void back(){
        if (!event.isClosed())
        mainCtrl.goToOverview();
        else
            mainCtrl.goToSettleDebts(event, server.getExpensesByEventId(event.getId()));
    }
}
