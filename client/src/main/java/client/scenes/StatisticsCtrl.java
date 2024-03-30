package client.scenes;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
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
        totalAmount = 0;
        if(!(this.pieChartData == null))
        pieChartData.clear();
        var expenses = server.getExpensesByEventId(event.getId());
        var tags = server.getTags(event.getId());
        List<String> names = tags.stream().map(x -> x.getName()).distinct().toList();
        pieChartData = FXCollections.observableArrayList();
        for(String name : names) {
            if(name.equals("gifting money"))
            continue;
            double amount = expenses.stream().filter(x -> x.getTag().getName().equals(name))
            .mapToDouble(x  -> (int) x.getAmount()).sum(); 
            pieChartData.add(new PieChart.Data(name + ": " + amount, amount));
            totalAmount += amount;
        }
        long remainingPercentage = 100;
        String lastName = "";
        for(PieChart.Data data : pieChartData) {
            long percentage = Math.round((data.getPieValue() / totalAmount)*100);
            lastName = data.getName();
            data.setName(data.getName() + " - " + percentage + "%");
            remainingPercentage = remainingPercentage - percentage;
        }
        if(remainingPercentage != 0 && pieChartData != null) {
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
    }

    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
        String piechartString = "Text.statisticsTitle";
        if(event !=null){
            pieChart.setTitle(MainCtrl.resourceBundle.getString(piechartString) + event.getTitle());
        }
        else {
            pieChart.setTitle(MainCtrl.resourceBundle.getString(piechartString));
        }
        eventTotalAmount.setText(MainCtrl.resourceBundle.getString("Text.statisticsTotal") + 
        totalAmount);
        back.setText(MainCtrl.resourceBundle.getString("button.back"));
        refresh.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        String stageTitleString = "title.statistics";
        if(event !=null){
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString) +  event.getTitle());
        }
        else{
            mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString(stageTitleString));
        }
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
