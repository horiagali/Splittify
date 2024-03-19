package client.scenes;

import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.Main;
import client.utils.ServerUtils;
import commons.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

public class QuoteOverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField eventName;
    private ObservableList<Event> data;

    @FXML
    private TableView<Event> table;
    @FXML
    private TableColumn<Event, String> colName;
    @FXML
    private TableColumn<Event, String> colLocation;
    @FXML
    private TableColumn<Event, String> colDate;
    @FXML
    private ToggleGroup languageGroup;
    @FXML
    private Button createEventButton;

    @FXML
    private Button joinEventButton;
    @FXML
    private Button refreshButton;

    @FXML
    private Button adminButton;

    @FXML
    private javafx.scene.text.Text yourEventsText;
    @FXML
    private Menu languageMenu;

    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public QuoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
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

        createEventButton.setText(MainCtrl.resourceBundle.getString("button.createEvent"));
        joinEventButton.setText(MainCtrl.resourceBundle.getString("button.joinEvent"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        adminButton.setText(MainCtrl.resourceBundle.getString("button.admin"));
        yourEventsText.setText(MainCtrl.resourceBundle.getString("Text.yourEvents"));
        colDate.setText(MainCtrl.resourceBundle.getString("TableColumn.colDate"));
        colName.setText(MainCtrl.resourceBundle.getString("TableColumn.colName"));
        colLocation.setText(MainCtrl.resourceBundle.getString("TableColumn.colLocation"));
        languageMenu.setText(MainCtrl.resourceBundle.getString("menu.languageMenu"));
    }


    /**
     * adds an event to the table
     */
    public void addEvent() {
        mainCtrl.showAddEvent();
    }

    private void clearFields() {
        eventName.clear();
    }


    /**
     * @return return event
     */
    private Event getEvent() {
        return new Event(eventName.getText(), "empty description", "empty location", new Date());

    }

    /**
     *
     */
    @SuppressWarnings("ParameterNumber")
    @Override

    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getTitle()));
        colLocation.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getLocation()));
        colDate.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getDescription()));
        table.setOnMouseClicked(this::handleTableItemClick);

    }

    /**
     * @param event event handler for mouse double click
     */
    private void handleTableItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click
            Event selectedEvent = table.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                mainCtrl.showEventOverview(selectedEvent);
            }
        }
    }

    /**
     *
     */
    public void addQuote() {
        mainCtrl.showAdd();
    }

    /**
     *
     */
    public void refresh() {
        var events = server.getEvents();
        data = FXCollections.observableList(events);
        table.setItems(data);
    }


    /**
     *
     */
    public void page() {
        mainCtrl.showPage();
    }

    /**
     *
     */


    /**
     *
     */
    public void showAddExpenses() {
        mainCtrl.showAddExpenses();
    }

    /**
     *
     */
    public void goToContact() {
        mainCtrl.goToContact();
    }

    /**
     *
     */
    public void goToOverview() {
        mainCtrl.goToOverview();
    }

    /**
     * goes to the admin pass page
     */
    public void goToAdminPass() {
        mainCtrl.goToAdminPass();
    }


}