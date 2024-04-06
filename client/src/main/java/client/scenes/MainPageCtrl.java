package client.scenes;

import client.Main;
import client.utils.Currency;
import client.utils.EmailUtils;
import client.utils.ServerUtils;

import com.google.inject.Inject;
import commons.Event;
import commons.Mail;
import jakarta.ws.rs.WebApplicationException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainPageCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<Event> data;

    @FXML
    private VBox vbox;
    @FXML
    private Button testEmailButton;
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
    private Menu currencyMenu;
    @FXML
    private Button createEventButton;
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Button disconnectButton;

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
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
    }


    /**
     * Method to update UI elements with the new language from the resource bundle
     */
    public void updateUIWithNewLanguage() {

        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.mainPage"));
        disconnectButton.setText(MainCtrl.resourceBundle.getString("button.disconnect"));
        createEventButton.setText(MainCtrl.resourceBundle.getString("button.createEvent"));
        joinEventButton.setText(MainCtrl.resourceBundle.getString("button.joinEvent"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        adminButton.setText(MainCtrl.resourceBundle.getString("button.admin"));
        yourEventsText.setText(MainCtrl.resourceBundle.getString("Text.yourEvents"));
        colDate.setText(MainCtrl.resourceBundle.getString("TableColumn.colDate"));
        colName.setText(MainCtrl.resourceBundle.getString("TableColumn.colName"));
        colLocation.setText(MainCtrl.resourceBundle.getString("TableColumn.colLocation"));
        currencyMenu.setText(MainCtrl.resourceBundle.getString("menu.currencyMenu"));
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
     * good credentials
     * @return true if good, false otherwise
     */
    private boolean goodCredentials(){
        if (EmailUtils.getHost() == null || EmailUtils.getPort() == null ||
                EmailUtils.getPassword() == null || EmailUtils.getUsername() == null)
            return false;
        return isValidEmail(EmailUtils.getUsername());
    }
    /**
     * Checks whether email is valid
     * @param email email to check
     * @return true iff valid
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*" +
                "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
                if (!server.getEvent(eventCode).isClosed())
                    mainCtrl.showEventOverview(server.getEvent(eventCode));
                else
                    mainCtrl.goToSettleDebts(server.getEvent(eventCode),
                            server.getExpensesByEventId(eventCode));
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
        if (!goodCredentials()){
            testEmailButton.setDisable(true);
            testEmailButton.setStyle("-fx-background-color: grey;");
        }
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
                if (!selectedEvent.isClosed())
                    mainCtrl.showEventOverview(selectedEvent);
                else
                {
                    mainCtrl.goToSettleDebts(selectedEvent,
                            server.getExpensesByEventId(selectedEvent.getId()));
                }

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

        Currency.setCurrencyUsed(currency.toUpperCase());

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
        File fileLang = new File(newLangPath);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Download Template File");
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("properties files (*.properties)",
                        "*.properties"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());
        String saveDir = file.toString();
        if (file != null) {
            try {
                Files.move(fileLang.toPath(), Paths.get(saveDir),
                        StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File downloaded to: " + saveDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * test the default email
     */
    public void testEmail(){
        Mail mail = new Mail("ooppteam56@gmail.com","Test Email", "This is a default email.");
        EmailUtils.sendEmail(mail);
    }
}
