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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import client.utils.ServerUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static Config config = new Config();
    public static String configLocation; 

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
            stream = new FileInputStream(file);
            config = mapper.readValue(stream, Config.class);
            System.out.println(config.getLanguage());
            System.out.println(config.getServerUrl());
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServerUtils.setServer(config.getServerUrl());
        String language = config.getLanguage();

        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var page = FXML.load(PageCtrl.class, "client", "scenes", "Page.fxml");
        var addExpense = FXML.load
                (AddExpensesCtrl.class, "client", "scenes", "AddExpenses.fxml");
        var contactDetails = FXML.load
                (ContactDetailsCtrl.class, "client", "scenes", "ContactDetails.fxml");
        var overviewApp = FXML.load
                (OverviewCtrl.class, "client", "scenes", "Overview.fxml");

        var invite = FXML.load(InviteCtrl.class, "client", "scenes", "Invite.fxml");
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        var adminPage = FXML.load(AdminPageCtrl.class, "client", "scenes", "AdminPage.fxml");
        var adminPass = FXML.load(AdminPassCtrl.class, "client", "scenes", "AdminPass.fxml");
        var addEvent = FXML.load(AddEventCtrl.class, "client", "scenes", "AddEventPage.fxml");
        var balances = FXML.load(BalancesCtrl.class, "client", "scenes", "Balances.fxml");
        var debts = FXML.load(SettleDebtsCtrl.class, "client", "scenes", "SettleDebts.fxml");
        mainCtrl.initialize(primaryStage, overview, add, page, addExpense, 
        contactDetails, overviewApp, invite, adminPage, adminPass, addEvent, balances,debts,
                language);

    }
}