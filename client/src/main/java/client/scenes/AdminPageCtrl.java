package client.scenes;

import client.Main;
import client.utils.EventExportData;
import client.utils.ServerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AdminPageCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<Event> data;

    @FXML
    private VBox vbox;
    @FXML
    private TableView<Event> table;
    @FXML
    TableColumn<Event, String> colId;
    @FXML
    private TableColumn<Event, String> colName;
    @FXML
    private TableColumn<Event, String> colDate;
    @FXML
    private TableColumn<Event, String> colLastChange;
    @FXML
    private ComboBox<String> sortingComboBox;
    @FXML
    private Menu languageMenu;
    @FXML
    private Button backButton;
    @FXML
    private ImageView languageFlagImageView;
    @FXML
    private Button downloadJsonButton;
    @FXML
    private Button importJsonButton;
    @FXML
    private Button refreshButton;

    /**
     * constructor
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AdminPageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * goes to overview
     */
    public void goBack() {
        mainCtrl.goToAdminPass();
        OverviewCtrl.setIsAdmin(false);
    }

    /**
     * initialises
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getTitle()));
        colId.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getId().toString()));
        colDate.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getCreationDate().toString()));
        colLastChange.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getDate().toString()));

        refresh();

        List<String> sortingOptions = new ArrayList<>();
        sortingOptions.add("A-Z");
        sortingOptions.add("Z-A");
        sortingOptions.add("New-Old");
        sortingOptions.add("Old-New");
        sortingOptions.add("Most Recent Change");
        sortingOptions.add("Least Recent Change");

        sortingComboBox.setItems(FXCollections.observableList(sortingOptions));
        sortingComboBox.setValue("Old-New");

        table.setOnMouseClicked(this::handleTableItemClick);

        addContextMenu();

        addKeyboardNavigationHandlers();

        server.registerForUpdates(this::addData);
    }

    /**
     * Handles data from long polling
     * @param e event
     */
    private void addData(Event e) {
        refresh();
        handleSort();
    }


    /**
     * Handles sorting, ae for javafx
     * @param e Action event
     */
    
    @FXML
    private void handleSort(ActionEvent e) {
        handleSort();
    }

    /**
     * Handles sorting, not for javafx
     */
    private void handleSort() {
        refresh();
        if (MainCtrl.resourceBundle != null) {
            getTranslations();
        }

        else
            switch (sortingComboBox.getValue()) {
            case "A-Z" -> sortAlphabetically();
            case "Z-A" -> sortAlphabeticallyReverse();
            case "New-Old" -> sortNewToOld();
            case "Old-New" -> sortOldToNew();
            case "Most Recent Change" -> sortMostRecentChange();
            case "Least Recent Change" -> sortLeastRecentChange();
        }


    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    private void getTranslations() {
        String newString = MainCtrl.resourceBundle.getString("Text.new");
        String oldString = MainCtrl.resourceBundle.getString("Text.old");
        String oldToNew = oldString + "-" + newString;
        String newToOld = newString + "-" + oldString;
        String mostRecentChange = MainCtrl.resourceBundle.getString("Text.mostRecentChange");
        String leastRecentChange = MainCtrl.resourceBundle.getString("Text.leastRecentChange");
        String value = sortingComboBox.getValue();

        if (value == null)
            return;
        if (value.equals("A-Z"))
            sortAlphabetically();
        if (value.equals("Z-A"))
            sortAlphabeticallyReverse();
        if (value.equals(oldToNew))
            sortOldToNew();
        if (value.equals(newToOld))
            sortNewToOld();
        if (value.equals(mostRecentChange))
            sortMostRecentChange();
        if (value.equals(leastRecentChange))
            sortLeastRecentChange();
    }


    /**
     * Sort data from a to z
     */
    public void sortAlphabetically() {
        data.sort(Comparator.comparing(e -> e.getTitle().toLowerCase()));
    }

    /**
     * Sorts date from z to a
     */
    private void sortAlphabeticallyReverse() {
        data.sort(Comparator.comparing(Event::getTitle,
                Comparator.comparing(String::toLowerCase)).reversed());
    }

    /**
     * Sort a date from new to old
     */
    private void sortNewToOld() {
        data.sort(Comparator.comparing(Event::getCreationDate).reversed());
    }

    /**
     * Sort data from old to new
     */
    private void sortOldToNew() {
        data.sort(Comparator.comparing(Event::getCreationDate));
    }

    /**
     * Sort data from most recent change to oldest.
     */
    private void sortMostRecentChange() {
        data.sort(Comparator.comparing(Event::getDate).reversed());
    }

    private void sortLeastRecentChange() {
        data.sort(Comparator.comparing(Event::getDate));
    }

    /**
     * stops the server
     */
    public void stop() {
        server.stop();
    }

    /**
     * adds keyboard na
     */
    private void addKeyboardNavigationHandlers() {
        vbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                goBack();
            }
            if (event.isControlDown() && event.getCode() == KeyCode.D) {
                deleteSelectedEvent();
            }
        });
    }

    /**
     * changes language to whatever s selected
     *
     * @param event
     */
    @FXML
    public void changeLanguage(ActionEvent event) {
        RadioMenuItem selectedLanguageItem = (RadioMenuItem) event.getSource();
        String language = selectedLanguageItem.getText().toLowerCase();

        MainCtrl.resourceBundle = ResourceBundle.
                getBundle("messages_" + language, new Locale(language));
        Main.config.setLanguage(language);

        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
        updateUIWithNewLanguage();

    }

    /**
     * updates UI
     */
    public void updateUIWithNewLanguage() {
        mainCtrl.setStageTitle(MainCtrl.resourceBundle.getString("title.adminPage"));
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
        downloadJsonButton.setText(MainCtrl.resourceBundle.getString("button.downloadJson"));
        importJsonButton.setText(MainCtrl.resourceBundle.getString("button.importJson"));
        refreshButton.setText(MainCtrl.resourceBundle.getString("button.refresh"));
        colName.setText(MainCtrl.resourceBundle.getString("Text.eventName"));
        colId.setText(MainCtrl.resourceBundle.getString("Text.eventLocation"));
        colDate.setText(MainCtrl.resourceBundle.getString("Text.eventDate"));
        colLastChange.setText(MainCtrl.resourceBundle.getString("Text.lastChange"));

        int num = sortingComboBox.getSelectionModel().getSelectedIndex();
        List<String> sortingOptions = new ArrayList<>();
        String newString = MainCtrl.resourceBundle.getString("Text.new");
        String oldString = MainCtrl.resourceBundle.getString("Text.old");
        sortingOptions.add("A-Z");
        sortingOptions.add("Z-A");
        sortingOptions.add(newString + "-" + oldString);
        sortingOptions.add(oldString + "-" + newString);
        sortingOptions.add(MainCtrl.resourceBundle.getString("Text.mostRecentChange"));
        sortingOptions.add(MainCtrl.resourceBundle.getString("Text.leastRecentChange"));

        sortingComboBox.setItems(FXCollections.observableList(sortingOptions));
        sortingComboBox.setPromptText(MainCtrl.resourceBundle.getString("Text.sortBy"));
        sortingComboBox.getSelectionModel().select(num);
    }

    /**
     * updates the flag
     *
     * @param language
     */
    public void updateFlagImageURL(String language) {
        String flagImageUrl = "/client/scenes/images/BritishFlag.png"; // Default
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

    @FXML
    private void importJson(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JSON File");
        fileChooser.getExtensionFilters().add
                (new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showOpenDialog(table.getScene().getWindow());
        if (file != null) {
            try {
                String json = new String(Files.readAllBytes(file.toPath()));
                ObjectMapper objectMapper = new ObjectMapper();
                EventExportData expData = objectMapper.readValue(json, EventExportData.class);
                Event importedEvent = expData.getEvent();
                List<Tag> tags = expData.getTags();
                List<Participant> participants = expData.getParticipants();
                List<Expense> expenses = expData.getExpenses();
                Event newEvent = new Event(
                        importedEvent.getTitle(),
                        importedEvent.getDescription(),
                        importedEvent.getDescription(),
                        new Date());
                Event addedEvent = new Event();
                try {
                    addedEvent = server.addEvent(newEvent);
                } catch (WebApplicationException e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
                addTags(tags,addedEvent.getId());
                addParticipants(participants,addedEvent.getId());
                addExpenses(expenses,addedEvent.getId());

                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * adds epenses to the event
     * @param expenses
     * @param id
     */
    private void addExpenses(List<Expense> expenses, Long id) {
        for (Expense expense : expenses) {
            Expense e = new Expense(expense.getTitle(),expense.getAmount(),expense.getDate()
                    ,expense.getPayer(),expense.getOwers(),expense.getTag());
            Expense added = server.addExpenseToEvent(id,e);
            added.settleBalance();

        }
    }

    /**
     * adds participants to the event
     * @param participants
     * @param id
     */
    private void addParticipants(List<Participant> participants, Long id) {
        for (Participant participant : participants) {
            Participant p = new Participant(participant.getNickname(),
                    participant.getEmail(),participant.getBic(),
                    participant.getIban(),participant.getBalance());
            server.addParticipant(id, p);
        }
    }

    /**
     * adds the non default tags to the event
     * @param tags
     * @param id
     */
    private void addTags(List<Tag> tags, Long id) {
        List<String> predefinedNames = Arrays.asList(
                "no tag",
                "gifting money",
                "food",
                "travel",
                "entrance fees"
        );
        for (Tag tag : tags) {
            if (!predefinedNames.contains(tag.getName())) {
                server.addTag(id, tag);
            }
        }
    }



    @FXML
    private void downloadJson(ActionEvent event) {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                EventExportData exportData = new
                        EventExportData(selectedEvent, selectedEvent.getTags(),
                        selectedEvent.getParticipants(), selectedEvent.getExpenses());
                System.out.println(exportData + "mataatatata");
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                String json = objectMapper.writeValueAsString(exportData);

                // Save JSON to file
                saveJsonToFile(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(MainCtrl.resourceBundle.getString("Text.warning"));
            alert.setHeaderText(null);
            alert.setContentText(MainCtrl.resourceBundle.getString("Text.eventDownloadError"));
            alert.showAndWait();
        }
    }

    private void saveJsonToFile(String json) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON File");
        fileChooser.getExtensionFilters().
                add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showSaveDialog(table.getScene().getWindow());

        if (file != null) {
            try {
                Path filePath = Paths.get(file.getAbsolutePath());
                Files.write(filePath, json.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addContextMenu() {
        String delete = "Delete";
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem(delete);
        deleteMenuItem.setOnAction(event -> deleteSelectedEvent());
        contextMenu.getItems().add(deleteMenuItem);
        table.setContextMenu(contextMenu);
    }

    private void deleteSelectedEvent() {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(MainCtrl.resourceBundle.getString("Text.confirmation"));
        confirmationDialog.setHeaderText
                (MainCtrl.resourceBundle.getString("Text.areYouSureDeleteEvent"));
        confirmationDialog.setContentText(MainCtrl.resourceBundle.getString("Text.noUndone"));

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                server.deleteEvent(selectedEvent);
                Main.config.removeId(selectedEvent.getId());
                refresh();
            } else {
                System.out.println(MainCtrl.resourceBundle.getString("Text.eventDeleteCanceled"));
            }
        });
    }

    private void handleTableItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Event selectedEvent = table.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                mainCtrl.showEventOverview(selectedEvent);
            }
        }
    }

    /**
     * refreshes
     */
    public void refresh() {
        var events = server.getEvents();
        data = FXCollections.observableList(events);
        table.setItems(data);
    }
}
