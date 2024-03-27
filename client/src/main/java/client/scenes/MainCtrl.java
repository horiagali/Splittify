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
package client.scenes;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import client.Main;
import commons.Event;
import commons.Participant;
import commons.Expense;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {
    Main main;

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private PageCtrl pageCtrl;
    private Scene page;
    private Scene adminPage;



    private AdminPageCtrl adminPageCtrl;

    private Scene adminPass;
    private AdminPassCtrl adminPassCtrl;

    private AddExpensesCtrl addExpensesCtrl;
    private Scene addExpenses;

    private Scene contactDetails;
    private ContactDetailsCtrl contactDetailsCtrl;

    private Scene overviewApp;
    private OverviewCtrl overviewAppCtrl;

    private Scene tagOverview;
    private TagOverviewCtrl tagOverviewCtrl;

    private Scene editParticipant;
    private EditParticipantCtrl editParticipantCtrl;
    private Scene serverSetter;
    private ServerSetterCtrl serverSetterCtrl;
    Pair<ServerSetterCtrl, Parent> serverPair;

    private Scene statistics;
    private StatisticsCtrl statisticsCtrl;

    private Scene invite;
    private InviteCtrl inviteCtrl;
    private AddEventCtrl addEventCtrl;
    private Scene addEvent;

    public static ResourceBundle resourceBundle;
    private Scene balances;
    private BalancesCtrl balancesCtrl;
    private Scene debts;
    private SettleDebtsCtrl debtsCtrl;


    /**
     * 
     * @param primaryStage
     * @param overview
     * @param add
     * @param page
     * @param addExpense
     * @param contactDetails
     * @param overviewApp
     * @param invite
     * @param adminPage
     * @param adminPass
     * @param language
     * @param addEvent
     * @param balances
     * @param editParticipant
     * @param serverSetter2 
     * @param statistics
     * @param debtsCtrlParentPair
     * @param tagOverview
     * @param main
     */

    @SuppressWarnings({"ParameterNumber", "checkstyle:MethodLength"})
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<PageCtrl, Parent> page,
                           Pair<AddExpensesCtrl, Parent> addExpense,
                           Pair<ContactDetailsCtrl, Parent> contactDetails,
                           Pair<OverviewCtrl, Parent> overviewApp,
                           Pair<InviteCtrl, Parent> invite,
                           Pair<AdminPageCtrl, Parent> adminPage,
                           Pair<AdminPassCtrl, Parent> adminPass,
                           Pair<AddEventCtrl, Parent> addEvent,
                           Pair<BalancesCtrl, Parent> balances,
                           Pair<EditParticipantCtrl,Parent> editParticipant,
                            Pair<ServerSetterCtrl, Parent> serverSetter2, String language,
                           Pair<StatisticsCtrl, Parent> statistics,
                           Pair<SettleDebtsCtrl, Parent> debtsCtrlParentPair,
                           Pair<TagOverviewCtrl, Parent> tagOverview, Main main) {

        this.primaryStage = primaryStage;
        this.main = main;
        resourceBundle = ResourceBundle.getBundle("messages_" + 
        language, new Locale(language));

        System.out.println("resourceBundle set to language " + language);
        overview.getKey().updateUIWithNewLanguage();
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.tagOverviewCtrl = tagOverview.getKey();
        this.tagOverview = new Scene(tagOverview.getValue());

        this.pageCtrl = page.getKey();
        this.page = new Scene(page.getValue());

        this.addEventCtrl = addEvent.getKey();
        this.addEvent = new Scene(addEvent.getValue());

        this.addExpensesCtrl = addExpense.getKey();
        this.addExpenses = new Scene(addExpense.getValue());

        this.contactDetailsCtrl = contactDetails.getKey();
        this.contactDetails = new Scene(contactDetails.getValue());

        this.overviewAppCtrl = overviewApp.getKey();
        this.overviewApp = new Scene(overviewApp.getValue());

        this.inviteCtrl = invite.getKey();
        this.invite = new Scene(invite.getValue());

        this.adminPageCtrl = adminPage.getKey();
        this.adminPage = new Scene(adminPage.getValue());

        this.adminPassCtrl = adminPass.getKey();
        this.adminPass = new Scene(adminPass.getValue());

        this.editParticipantCtrl = editParticipant.getKey();
        this.editParticipant = new Scene(editParticipant.getValue());

        this.balancesCtrl = balances.getKey();
        this.balances = new Scene(balances.getValue());


        this.serverPair = serverSetter2;

        this.statisticsCtrl = statistics.getKey();
        this.statistics = new Scene(statistics.getValue());
        this.debtsCtrl = debtsCtrlParentPair.getKey();
        this.debts = new Scene(debtsCtrlParentPair.getValue());

        showOverview();
        primaryStage.show();
    }

    /**
     * initializes scene to set serverUrl of client
     * @param primaryStage
     * @param serverSetter
     * @param main 
     */
    public void initializeServerSetter(Stage primaryStage, 
    Pair<ServerSetterCtrl, Parent> serverSetter, Main main) {
        resourceBundle = ResourceBundle.getBundle("messages_" + 
        Main.config.getLanguage(), new Locale(Main.config.getLanguage()));
        serverSetter.getKey().updateUIWithNewLanguage();
        this.primaryStage = primaryStage;
        this.serverSetterCtrl = serverSetter.getKey();
        serverSetter.getKey().serverURL.setText(Main.config.getServerUrl());
        this.serverSetter = new Scene(serverSetter.getValue());
        primaryStage.setTitle("Choose your server");
        primaryStage.setScene(this.serverSetter);
        primaryStage.show();
        setMain(main);
    }

    /**
     * 
     */
    public void showServerSetter() {
        initializeServerSetter(primaryStage, serverPair, main);
    }

    /**
     * 
     * @return main
     */
    public Main getMain() {
        return main;
    }

    

    /**
     * 
     * @param main main
     */
    public void setMain(Main main) {
        this.main = main;
    }


    /**
     * 
     */
    public void showOverview() {
        primaryStage.setTitle("Events: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    /**
     * 
     * @param name name
     */
    public void addToOverview(String name) {
        overviewAppCtrl.addName(name);
        overviewAppCtrl.refresh();
    }

    /**
     * 
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * 
     */
    public void addEvent() {
        primaryStage.setTitle("Event: Adding Events");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * Show add event page
     */
    public void showAddEvent() {
        primaryStage.setTitle("Create an event");
        primaryStage.setScene(addEvent);
    }

    /**
     * Show tag overview page
     * @param event the event the tags belong to
     */
    public void goToTagOverview(Event event) {
        primaryStage.setTitle("Tag Manager");
        TagOverviewCtrl.setEvent(event);
        primaryStage.setScene(tagOverview);
        tagOverviewCtrl.refresh();
    }
    /**
     * 
     */
    public void showPage() {
        primaryStage.setTitle("Iulia's Page");
        primaryStage.setScene(page);
    }

    /**
     * 
     */
    public void showAddExpenses() {
        primaryStage.setTitle("Expenses: Add Expense");
        primaryStage.setScene(addExpenses);
        addExpensesCtrl.refreshParticipants();
        // Additional setup for the Add Expense page, if needed
    }

    /**
     * 
     */
    public void goToContact() {
        primaryStage.setTitle("Contact Details");
        primaryStage.setScene(contactDetails);
    }


    /**
     * 
     */
    public void goToOverview() {
        primaryStage.setTitle("Overview");
        primaryStage.setScene(overviewApp);
        overviewAppCtrl.refresh();

    }

    /**
     * Navigates to the Edit Participant page.
     * @param participant The participant to be edited.
     * @param event the event the participant belongs to
     */
    public void goToEditParticipant(Participant participant, Event event) {
        primaryStage.setTitle("Edit Participant");
        primaryStage.setScene(editParticipant);
        EditParticipantCtrl.setParticipant(participant);
        EditParticipantCtrl.setEvent(event);
        editParticipantCtrl.displayParticipantDetails();
    }



    /**
     * send invites
     * @param eventName eventname
     * @param event event
     */
    public void sendInvites(Label eventName, Event event) {
        primaryStage.setTitle("Send Invites");
        inviteCtrl.setEvent(event);

        //inviteCtrl.setName(eventName);
        primaryStage.setScene(invite);
    }
    /**
     *
     * @param selectedEvent
     * goes to event overview, showing the event passed as a parameter
     */
    public void showEventOverview(Event selectedEvent) {
        primaryStage.setTitle(selectedEvent.getTitle());
        primaryStage.setScene(overviewApp);
        overviewAppCtrl.displayEvent(selectedEvent);
        overviewAppCtrl.refresh();

    }

    /**
     *  goes to admin password page
     */
    public void goToAdminPass() {
        primaryStage.setTitle("Admin password");
        primaryStage.setScene(adminPass);
        String pass = AdminPassCtrl.generatePassword();
        System.out.println("The password is " + pass);
        AdminPassCtrl.setPass(pass);
    }

    /**
     *   goes to admin page
     */
    public void goToAdminPage(){
        primaryStage.setTitle("Admin page");
        primaryStage.setScene(adminPage);
        adminPageCtrl.refresh();
    }

    /**
     * goes to balances page
     * @param event event
     */
    public void goToBalances(Event event) {
        primaryStage.setTitle("Balances page");
        balancesCtrl.setEvent(event);
        primaryStage.setScene(balances);
    }

    /**
     * goes to statistics page
     * @param event event to see statistics from
     */
    public void goToStatistics(Event event) {
        primaryStage.setTitle("Statistics of " + event.getTitle());
        StatisticsCtrl.setEvent(event);
        primaryStage.setScene(statistics);
        statisticsCtrl.refresh();

    }    

    /**
     * set the title of primary stage, needed for translations
     * @param title title
     */
    public void setStageTitle(String title) {
        primaryStage.setTitle(title);
    }

    /**
     * goes to settle debts
     * @param event event
     * @param expenses expenses
     */
    public void goToSettleDebts(Event event, List<Expense> expenses) {
        primaryStage.setTitle("Open Debts page");
        debtsCtrl.setEvent(event);
        debtsCtrl.setExpenses(expenses);
        primaryStage.setScene(debts);
    }
}