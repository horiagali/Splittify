package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


public class MainPageCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<Event> data;

    @FXML
    private VBox vbox;
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
    private TextField joinEventCode;
    @FXML
    private Button refreshButton;

    @FXML
    private Button adminButton;

    @FXML
    private javafx.scene.text.Text yourEventsText;
    @FXML
    private Menu languageMenu;

    @FXML
    private ToggleGroup currencyGroup;


    /**
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MainPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
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


    /**
     * Lets user view the event corresponding to the event id
     * @param ae actionEvent
     */
    public void joinEvent(ActionEvent ae) {
        try {
            Long eventCode = Long.parseLong(joinEventCode.getText());
            try {
                mainCtrl.showEventOverview(server.getEvent(eventCode));
            } catch (WebApplicationException e) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Please only enter numbers");
            alert.showAndWait();
        }
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
        addKeyboardNavigationHandlers();

        server.registerForEvents("/topic/events", e -> data.add(e));
    }

    /**
     * Add keyboard navigation
     */
    private void addKeyboardNavigationHandlers() {
        vbox.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                addEvent();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.J){
                ActionEvent dummyEvent = new ActionEvent();
                joinEvent(dummyEvent);
            }
            if (event.isControlDown() && event.isAltDown() && event.getCode() == KeyCode.D) {
                goToAdminPass();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.R) {
                refresh();
            }
            if (event.getCode() == KeyCode.ENTER) {
                handleTableItemKeyPress();
            }
        });
    }

    /**
     * event handler for keyboard press
     */
    private void handleTableItemKeyPress() {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            OverviewCtrl.setSelectedEvent(selectedEvent);
            mainCtrl.showEventOverview(selectedEvent);
        }

    }


    /**
     * @param event event handler for mouse double click
     */
    private void handleTableItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click
            Event selectedEvent = table.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                try {
                    selectedEvent.getId();
                } catch (NullPointerException e) {
                    System.out.println("Something went wrong. Please try again.");
                    refresh();
                    return;
                }
                OverviewCtrl.setSelectedEvent(selectedEvent);
                mainCtrl.showEventOverview(selectedEvent);
            }
        }
    }

    /**
     * call the download of the json of the selected event
     * @param event
     */
    @FXML
    private void downloadJson(ActionEvent event) {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writeValueAsString(selectedEvent);

                downloadJsonFile(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select an event to download JSON.");
            alert.showAndWait();
        }
    }

    /**
     * imports an event from a json file
     * @param event
     */
    @FXML
    private void importJson(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON File");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Event importedEvent = objectMapper.readValue(file, Event.class);

                try {
                    server.addEvent(importedEvent);
                } catch (WebApplicationException e) {
                    e.printStackTrace();
                    return;
                }

                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        refresh();
    }



    /**
     * acually downloads the json
     * @param json
     */
    private void downloadJsonFile(String json) {
        // You can use JavaFX FileChooser to choose where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON File");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(json);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
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
    public void disconnect() {
        mainCtrl.showServerSetter();
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

    /**
     * goes to balances page
     * @param event
     */
    public void goToBalances(Event event){
        mainCtrl.goToBalances(event);
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
     * add new language
     * @param actionEvent
     */
    public void addNewLanguage(ActionEvent actionEvent) {
        Properties newLang = new Properties();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader("client/src" +
                             "/main/resources/langTemplate.txt"))) {
            newLang.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String newLangPath;
        try (OutputStream output = new FileOutputStream("client/src/main/resources" +
                "/newLang.properties")) {
            newLang.store(output, "Add the name of your new language to " +
                    "the first line of this file as a comment\n"+
                    "Send the final translation version to ooppteam56@gmail.com");

            newLangPath = "client/src/main/resources/newLang.properties";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File(newLangPath);
        String saveDir = System.getProperty("user.home") + "/Downloads" + "/" + file.getName();
        try {
            Files.move(file.toPath(), Paths.get(saveDir), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File downloaded to: " + saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
