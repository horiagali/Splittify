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
import javafx.scene.control.RadioMenuItem;

public class StatisticsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private static Event event;
    ObservableList<PieChart.Data> pieChartData;

    @FXML
    private PieChart pieChart;

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
     */
    public void refresh() {
        if(!(this.pieChartData == null))
        pieChartData.clear();
        var expenses = server.getExpensesByEventId(event.getId());
        var tags = server.getTags(event.getId());
        List<String> names = tags.stream().map(x -> x.getName()).distinct().toList();
        pieChartData = FXCollections.observableArrayList();
        for(String name : names) {
            int amount = expenses.stream().filter(x -> x.getTag().getName().equals(name))
            .mapToInt(x  -> (int) x.getAmount()).sum();
            pieChartData.add(new PieChart.Data(name, amount));
        }
        pieChart.setData(pieChartData);
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
