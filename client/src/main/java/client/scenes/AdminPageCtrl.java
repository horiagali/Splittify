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
import java.util.stream.Collectors;

public class AdminPageCtrl implements Initializable {
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
    private Menu languageMenu;
    @FXML
    private Button backButton;
    @FXML
    private ImageView languageFlagImageView;

    /**
     * constructor
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
        mainCtrl.showOverview();
    }

    /**
     * initialises
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getTitle()));
        colLocation.setCellValueFactory(q ->
                new SimpleStringProperty(q.getValue().getLocation()));
        colDate.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getDescription()));

        table.setOnMouseClicked(this::handleTableItemClick);

        addContextMenu();

        refresh();
        addKeyboardNavigationHandlers();

        server.registerForUpdates(data::add);
    }

    private List<Event> reverse(List<Event> l) {
        Stack<Event> eventStack = new Stack<>();
        List<Event> sorted = new ArrayList<>();
        for (Event event: l) {
            eventStack.push(event);
        }
        for (int i = 0; i < l.size(); i++) {
            sorted.add(i, eventStack.pop());
        }
        return sorted;
    }
    public List<Event> sortOldToNew(List<Event> l) {
        return reverse(l);
    }

    public List<Event> sortAlphabetically(List<Event> l) {
        List<Event> sorted = l.stream()
                .sorted(Comparator.comparing(Event::getTitle))
                .collect(Collectors.toList());
        return sorted;
    }

    public List<Event> sortAlphabeticallyReverse(List<Event> l) {
        List<Event> sorted = l.stream()
                .sorted(Comparator.comparing(Event::getTitle))
                .collect(Collectors.toList());
        return reverse(sorted);
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
     * @param event
     */
    @FXML
    public void changeLanguage(ActionEvent event) {
        RadioMenuItem selectedLanguageItem = (RadioMenuItem) event.getSource();
        String language = selectedLanguageItem.getText().toLowerCase();

        MainCtrl.resourceBundle = ResourceBundle.
                getBundle("messages_" + language, new Locale(language));
        Main.config.setLanguage(language);

        updateUIWithNewLanguage();
        mainCtrl.updateLanguage(language);
        updateFlagImageURL(language);
    }

    /**
     * updates UI
     */
    public void updateUIWithNewLanguage() {
        backButton.setText(MainCtrl.resourceBundle.getString("button.back"));
    }

    /**
     * updates the flag
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
                        new Date()
                );
                Event addedEvent = new Event();
                try {
                         server.sendEvent("/app/events", newEvent);
                         List<Event> events = server.getEvents();
                         addedEvent = events.getLast();
                } catch (WebApplicationException e) {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
                for (Tag tag : tags) {
                    server.addTag(addedEvent.getId(), tag);
                }
                for (Participant participant : participants) {
                    server.addParticipant(addedEvent.getId(), participant);
                }
                for (Expense expense : expenses) {
                    server.addExpenseToEvent(addedEvent.getId(), expense);
                }

                // Refresh the table view
                refresh();
            } catch (IOException e) {
                e.printStackTrace();
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
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select an event to download JSON.");
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
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> deleteSelectedEvent());
        contextMenu.getItems().add(deleteMenuItem);
        table.setContextMenu(contextMenu);
    }

    private void deleteSelectedEvent() {
        Event selectedEvent = table.getSelectionModel().getSelectedItem();
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Are you sure you want to delete the event?");
        confirmationDialog.setContentText("This action cannot be undone.");

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                server.deleteEvent(selectedEvent);
                refresh();
            } else {
                System.out.println("Event deletion canceled.");
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
