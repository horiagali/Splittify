/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import client.scenes.*;
import client.utils.Currency;
import client.utils.ServerUtils;
import jakarta.ws.rs.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static Config config = new Config();
    public static String configLocation; 
    MainCtrl mainCtrl;
    Stage primaryStage;

    /**
     * The entry point of the application.
     *
     * @param args Command-line arguments.
     * @throws URISyntaxException If a URI syntax error occurs.
     * @throws IOException        If an I/O error occurs.
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage The primary stage of the application.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Currency.scheduleDailyUpdate();
        this.primaryStage = primaryStage;
        var mapper = new ObjectMapper();
        FileInputStream stream;
        try {
            configLocation = Main.class.getProtectionDomain().getCodeSource()
            .getLocation().toURI().getPath() + "data/client";
            var file = new File(configLocation + "/config.json");
            // If file is not present, construct from default values
            if(!file.exists()) {
                var dir = new File(configLocation);
                dir.mkdirs();
                file.createNewFile();
                mapper.writeValue(file, new Config());
            }
            mainCtrl = INJECTOR.getInstance(MainCtrl.class);
            stream = new FileInputStream(file);
            config = mapper.readValue(stream, Config.class);
            ServerUtils.setServer(config.getServerUrl());
            System.out.println(config.getLanguage());
            System.out.println(config.getServerUrl());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();}
        if(checkConnection()) {
            loadScenes();
        }else{
            var serverSetter = FXML.load(ServerSetterCtrl.class, 
            "client", "scenes", "ServerSetter.fxml");
            mainCtrl.initializeServerSetter(primaryStage, serverSetter, this);
        }



    }

    /**
     * load all scenes
     */
    public void loadScenes() {

        var overview = FXML.load(MainPageCtrl.class, "client", "scenes", "MainPage.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var page = FXML.load(PageCtrl.class, "client", "scenes", "Page.fxml");
        var addExpense = FXML.load
                (AddExpensesCtrl.class, "client", "scenes", "AddExpenses.fxml");
        var contactDetails = FXML.load
                (ContactDetailsCtrl.class, "client", "scenes", "ContactDetails.fxml");
        var overviewApp = FXML.load
                (OverviewCtrl.class, "client", "scenes", "Overview.fxml");
        var invite = FXML.load(InviteCtrl.class, "client", "scenes", "Invite.fxml");
        var adminPage = FXML.load(AdminPageCtrl.class, "client", "scenes", "AdminPage.fxml");
        var adminPass = FXML.load(AdminPassCtrl.class, "client", "scenes", "AdminPass.fxml");
        var addEvent = FXML.load(AddEventCtrl.class, "client", "scenes", "AddEventPage.fxml");
        var balances = FXML.load(BalancesCtrl.class, "client", "scenes", "Balances.fxml");
        var editParticipant = FXML.load(EditParticipantCtrl.class,
                "client", "scenes", "EditParticipant.fxml");

        var debts = FXML.load(SettleDebtsCtrl.class, "client", "scenes", "SettleDebts.fxml");
        var statistics = FXML.load(StatisticsCtrl.class, "client", "scenes", "Statistics.fxml");
        var serverSetter = FXML.load(ServerSetterCtrl.class, "client", 
        "scenes", "ServerSetter.fxml");
        var tagOverview = FXML.load(TagOverviewCtrl.class, "client", "scenes", "TagOverview.fxml");
        mainCtrl.initialize(primaryStage, overview, add, page, addExpense, 
        contactDetails, overviewApp, invite, adminPage, adminPass, addEvent, balances, 
        editParticipant,serverSetter, config.getLanguage(),  statistics, debts, tagOverview, this);

        primaryStage.setOnCloseRequest(e -> {
            adminPage.getKey().stop();
        });
    }

    /**
     * Method to check whether the url the client wants to connect is an active server.
     * If it is not, the ServerSetter scene has to be shown.
     * @return boolean, false: invalid url/ true: valid url
     */
    public static boolean checkConnection() {
        String uri = config.getServerUrl() + "/api/events";
		URL url;
        try {
            url = new URI(uri).toURL();
            var is = url.openConnection().getInputStream();
		    var br = new BufferedReader(new InputStreamReader(is));
		    String line;
		    while ((line = br.readLine()) != null) {
			    System.out.println(line);
		    }
        }  catch (BadRequestException e) {
            return true;
        }
        catch (ConnectException e) {
            //no connection
            return false;
        }
        catch (Exception e) {
            //connection but file not found, does not matter
            return true;
        }
        return true;
		
    }
}