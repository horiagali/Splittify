package client.scenes;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
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
    private PieChart pieChart;

    @FXML
    private Button back;

    @FXML
    private Button refresh;

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

    }

    /**
     * creates the data to put in the piechart
     * @param expenses list of expenses of this event.
     * @param tags list of tags of this event.
     */
    private void createData(List<Expense> expenses, List<Tag> tags) {
        //method to group all expenses on tag and get their total amount
        for(Tag tag : tags) {
            if(tag.getName().equals("gifting money"))
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
        if(remainingPercentage != 0 && pieChartData != null) {
            PieChart.Data dataToEdit = pieChartData.get(pieChartData.size()-1);
            long percentage = Math.round((dataToEdit.getPieValue() / totalAmount)*100);
            remainingPercentage = remainingPercentage + percentage;
            dataToEdit.setName(lastName + " - " + remainingPercentage + "%");
            remainingPercentage = 0;
            
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
        pieChart.setTitle(MainCtrl.resourceBundle.getString("Text.statisticsTitle") + 
        event.getTitle());
        eventTotalAmount.setText(MainCtrl.resourceBundle.getString("Text.statisticsTotal") + 
        totalAmount);
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        refresh.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.statistics") 
        + event.getTitle());
        
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
        mainCtrl.goToOverview();
    }
}
