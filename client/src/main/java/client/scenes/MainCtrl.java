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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import client.Main;
import commons.Event;
import commons.Participant;
import commons.Expense;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {
    Main main;

    private Stage primaryStage;

    private MainPageCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private PageCtrl pageCtrl;
    private Scene page;
    private Scene adminPage;
    private AdminPageCtrl adminPageCtrl;

    private Scene adminPass;
    private AdminPassCtrl adminPassCtrl;

    private Scene editEvent;
    private EditEventCtrl editEventCtrl;

    private AddExpensesCtrl addExpensesCtrl;
    private Scene addExpenses;
    private EditExpenseCtrl editExpenseCtrl;
    private Scene editExpense;

    private Scene contactDetails;
    private ContactDetailsCtrl contactDetailsCtrl;

    private Scene overviewApp;
    private OverviewCtrl overviewAppCtrl;

    private Scene tagOverview;
    private TagOverviewCtrl tagOverviewCtrl;

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

    private Scene addPartial;
    private AddPartialDebtCtrl addPartialDebtCtrl;

    private Scene editPartialDebt;
    private EditPartialDebtCtrl editPartialDebtCtrl;

    private Dimension dimension;


    /**
     *
     * @param primaryStage
     * @param overview
     * @param add
     * @param page
     * @param addExpense
     * @param editExpense
     * @param contactDetails
     * @param overviewApp
     * @param invite
     * @param adminPage
     * @param adminPass
     * @param language
     * @param addEvent
     * @param balances
     * @param serverSetter2
     * @param statistics
     * @param debtsCtrlParentPair
     * @param tagOverview
     * @param partialDebtCtrlParentPair
     * @param editPartialDebtCtrlParentPair
     * @param editEvent 
     * @param main
     *
     */

    @SuppressWarnings({"ParameterNumber", "checkstyle:MethodLength"})
    public void initialize(Stage primaryStage, Pair<MainPageCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<PageCtrl, Parent> page,
                           Pair<AddExpensesCtrl, Parent> addExpense,
                           Pair<EditExpenseCtrl, Parent> editExpense,
                           Pair<ContactDetailsCtrl, Parent> contactDetails,
                           Pair<OverviewCtrl, Parent> overviewApp,
                           Pair<InviteCtrl, Parent> invite,
                           Pair<AdminPageCtrl, Parent> adminPage,
                           Pair<AdminPassCtrl, Parent> adminPass,
                           Pair<AddEventCtrl, Parent> addEvent,
                           Pair<BalancesCtrl, Parent> balances,
                           Pair<ServerSetterCtrl, Parent> serverSetter2, String language,
                           Pair<StatisticsCtrl, Parent> statistics,
                           Pair<SettleDebtsCtrl, Parent> debtsCtrlParentPair,
                           Pair<TagOverviewCtrl, Parent> tagOverview,
                           Pair<AddPartialDebtCtrl, Parent> partialDebtCtrlParentPair,
                           Pair<EditPartialDebtCtrl, Parent> editPartialDebtCtrlParentPair,
                           Pair<EditEventCtrl, Parent> editEvent, Main main) {

        this.primaryStage = primaryStage;
        this.main = main;
        primaryStage.setMaximized(true);

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

        this.editEventCtrl = editEvent.getKey();
        this.editEvent = new Scene(editEvent.getValue());

        this.addEventCtrl = addEvent.getKey();
        this.addEvent = new Scene(addEvent.getValue());

        this.addExpensesCtrl = addExpense.getKey();
        this.addExpenses = new Scene(addExpense.getValue());

        this.editExpenseCtrl = editExpense.getKey();
        this.editExpense = new Scene(editExpense.getValue());

        this.contactDetailsCtrl = contactDetails.getKey();
        this.contactDetails = new Scene(contactDetails.getValue());

        this.overviewAppCtrl = overviewApp.getKey();
        this.overviewApp = new Scene(overviewApp.getValue());

        this.inviteCtrl = invite.getKey();
        this.invite = new Scene(invite.getValue());

        this.adminPageCtrl = adminPage.getKey();
        this.adminPage = new Scene(adminPage.getValue());

        adminPass.getKey().updateUIWithNewLanguage();
        this.adminPassCtrl = adminPass.getKey();
        this.adminPass = new Scene(adminPass.getValue());

        this.balancesCtrl = balances.getKey();
        this.balances = new Scene(balances.getValue());


        this.serverPair = serverSetter2;

        this.statisticsCtrl = statistics.getKey();
        this.statistics = new Scene(statistics.getValue());
        this.debtsCtrl = debtsCtrlParentPair.getKey();
        this.debts = new Scene(debtsCtrlParentPair.getValue());
        this.addPartial = new Scene(partialDebtCtrlParentPair.getValue());
        this.addPartialDebtCtrl = partialDebtCtrlParentPair.getKey();
        this.editPartialDebt = new Scene(editPartialDebtCtrlParentPair.getValue());
        this.editPartialDebtCtrl = editPartialDebtCtrlParentPair.getKey();
        updateFlags(language);

        dimension = Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setWidth(dimension.getWidth() / 1.4);
        primaryStage.setHeight(dimension.getHeight() / 1.4);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);

        showOverview();
        primaryStage.show();
    }

    /**
     * initializes scene to set serverUrl of client
     *
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
        primaryStage.setMaximized(true);
        this.serverSetter = new Scene(serverSetter.getValue());
        primaryStage.setTitle(resourceBundle.getString("title.serverSetter"));
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
     * @return main
     */
    public Main getMain() {
        return main;
    }


    /**
     * @param main main
     */
    public void setMain(Main main) {
        this.main = main;
    }


    /**
     *
     */
    public void showOverview() {
        primaryStage.setTitle(resourceBundle.getString("title.mainPage"));
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
        overviewAppCtrl.refresh();
    }

    /**
     * @param name name
     *             What does this do? - Mihnea
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
        primaryStage.setTitle(resourceBundle.getString("title.addEvent"));
        primaryStage.setScene(addEvent);
    }

    /**
     * Show tag overview page
     *
     * @param event the event the tags belong to
     */
    public void goToTagOverview(Event event) {
        primaryStage.setTitle(resourceBundle.getString("title.tagManager"));
        TagOverviewCtrl.setEvent(event);
        primaryStage.setScene(tagOverview);
        tagOverviewCtrl.refresh();
        TagOverviewCtrl.setIsActive(true);
    }

    /**
     * show edit event page
     * @param event
     */
    public void showEditEvent(Event event) {
        primaryStage.setTitle("Edit Event");
        editEventCtrl.setEvent(event);
        primaryStage.setScene(editEvent);
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
        primaryStage.setTitle(resourceBundle.getString("title.addExpense"));
        primaryStage.setScene(addExpenses);
        addExpensesCtrl.refreshParticipants();
        // Additional setup for the Add Expense page, if needed
    }

    /**
     *
     */
    public void goToContact() {
        primaryStage.setTitle("Contact Details");
        contactDetailsCtrl.loadInfo(null);
        primaryStage.setScene(contactDetails);
    }

    /**
     * go to Contact details but instead get the update view
     *
     * @param participant
     */
    public void goToContact(Participant participant) {
        primaryStage.setTitle(resourceBundle.getString("title.contactDetails"));
        contactDetailsCtrl.loadInfo(participant);
        primaryStage.setScene(contactDetails);
    }


    /**
     *
     */
    public void goToOverview() {
        primaryStage.setTitle(resourceBundle.getString("title.overview"));
        overviewAppCtrl.resetComboBoxes();
        overviewAppCtrl.refresh();
        primaryStage.setScene(overviewApp);
        OverviewCtrl.setIsActive(true);
        //overviewAppCtrl.refresh();
    }

    /**
     * send invites
     *
     * @param eventName eventname
     * @param event     event
     */
    public void sendInvites(Text eventName, Event event) {
        primaryStage.setTitle(resourceBundle.getString("title.sendInvites"));
        inviteCtrl.setEvent(event);

        primaryStage.setScene(invite);
        inviteCtrl.refresh();

    }

    /**
     * @param selectedEvent goes to event overview, showing the event passed as a parameter
     */
    public void showEventOverview(Event selectedEvent) {
        primaryStage.setTitle(selectedEvent.getTitle());
        primaryStage.setScene(overviewApp);
        overviewAppCtrl.refresh();
        overviewAppCtrl.resetComboBoxes();
        overviewAppCtrl.displayEvent(selectedEvent);
        OverviewCtrl.setIsActive(true);
    }

    /**
     * goes to admin password page
     */
    public void goToAdminPass() {
        primaryStage.setTitle(resourceBundle.getString("title.adminPassword"));
        primaryStage.setScene(adminPass);
        String pass = adminPassCtrl.generatePassword();
        //System.out.println("The password is " + pass);
        AdminPassCtrl.setPass(pass);
    }

    /**
     * goes to admin page
     */
    public void goToAdminPage() {
        primaryStage.setTitle(resourceBundle.getString("title.adminPage"));
        primaryStage.setScene(adminPage);
        adminPageCtrl.refresh();
        OverviewCtrl.setIsAdmin(true);
    }

    /**
     * goes to balances page
     *
     * @param event event
     */
    public void goToBalances(Event event) {
        primaryStage.setTitle(resourceBundle.getString("title.balances"));
        balancesCtrl.setEvent(event);
        primaryStage.setScene(balances);

        balancesCtrl.refresh();
        BalancesCtrl.setIsActive(true);

    }

    /**
     * goes to statistics page
     *
     * @param event event to see statistics from
     */
    public void goToStatistics(Event event) {
        primaryStage.setTitle(resourceBundle.getString
                ("title.statisticsOf") + " " + event.getTitle());
        statisticsCtrl.setEvent(event);
        statisticsCtrl.refresh();
        primaryStage.setScene(statistics);
        statisticsCtrl.refresh();
        StatisticsCtrl.setIsActive(true);
    }

    /**
     * set the title of primary stage, needed for translations
     *
     * @param title title
     */
    public void setStageTitle(String title) {
        primaryStage.setTitle(title);
    }

    /**
     * goes to settle debts
     *
     * @param event    event
     * @param expenses expenses
     */
    public void goToSettleDebts(Event event, List<Expense> expenses) {
        primaryStage.setTitle(resourceBundle.getString("title.openDebts"));
        debtsCtrl.setEvent(event);
        List<Expense> debtExpense = expenses.stream()
                .filter(x -> x.getTag().getName().equals("debt")).toList();
        debtsCtrl.setExpenses(debtExpense);
        primaryStage.setScene(debts);
        debtsCtrl.refresh();

    }

    /**
     * Goes to edit expense.
     *
     * @param event   the event of the expense.
     * @param expense the expense to edit.
     */
    public void goToEditExpense(Event event, Expense expense) {
        primaryStage.setTitle(resourceBundle.getString("title.editExpense"));
        EditExpenseCtrl.setEvent(event);
        EditExpenseCtrl.setExpense(expense);
        primaryStage.setScene(editExpense);
        editExpenseCtrl.display();
    }

    /**
     * method to update language
     *
     * @param language lanuage from button
     */
    public void updateLanguage(String language) {
        // Load the appropriate resource bundle based on the selected language
        resourceBundle = ResourceBundle.getBundle("messages_" + language, new Locale(language));

        // Notify controllers about the language change
        notifyControllers();
        updateFlags(language);
    }

    /**
     * updates flags
     *
     * @param language chosen language
     */
    private void updateFlags(String language) {
        inviteCtrl.updateFlagImageURL(language);
        adminPassCtrl.updateFlagImageURL(language);
        adminPageCtrl.updateFlagImageURL(language);
        statisticsCtrl.updateFlagImageURL(language);
        overviewAppCtrl.updateFlagImageURL(language);
        tagOverviewCtrl.updateFlagImageURL(language);
        addExpensesCtrl.updateFlagImageURL(language);
        overviewCtrl.updateFlagImageURL(language);
        balancesCtrl.updateFlagImageURL(language);
        serverPair.getKey().updateFlagImageURL(language);
        addEventCtrl.updateFlagImageURL(language);
        contactDetailsCtrl.updateFlagImageURL(language);
        editExpenseCtrl.updateFlagImageURL(language);
        balancesCtrl.updateFlagImageURL(language);
        debtsCtrl.updateFlagImageURL(language);
        editEventCtrl.updateFlagImageURL(language);
    }


    /**
     * Method to notify all controllers about the language change
     */
    private void notifyControllers() {
        adminPassCtrl.updateUIWithNewLanguage();
        overviewCtrl.updateUIWithNewLanguage();
        statisticsCtrl.updateUIWithNewLanguage();
        tagOverviewCtrl.updateUIWithNewLanguage();
        overviewAppCtrl.updateUIWithNewLanguage();
        inviteCtrl.updateUIWithNewLanguage();
        adminPageCtrl.updateUIWithNewLanguage();
        balancesCtrl.updateUIWithNewLanguage();
        serverPair.getKey().updateUIWithNewLanguage();
        addEventCtrl.updateUIWithNewLanguage();
        addExpensesCtrl.updateUIWithNewLanguage();
        overviewCtrl.updateUIWithNewLanguage();
        contactDetailsCtrl.updateUIWithNewLanguage();
        editExpenseCtrl.updateUIWithNewLanguage();
        balancesCtrl.updateUIWithNewLanguage();
        debtsCtrl.updateUIWithNewLanguage();
        editEventCtrl.updateUIWithNewLanguage();
    }

    /**
     * go to partial
     */
    public void goToPartial() {
        primaryStage.setTitle(resourceBundle.getString("title.addPartialDebt"));
        primaryStage.setScene(addPartial);
        addPartialDebtCtrl.refreshParticipants();
    }

    /**
     * go to edit partial debt
     *
     * @param event
     * @param expense
     */

    public void goToEditPartialDebt(Event event, Expense expense) {
        primaryStage.setTitle(resourceBundle.getString("title.editPartialDebt"));
        EditPartialDebtCtrl.setEvent(event);
        EditPartialDebtCtrl.setExpense(expense);
        primaryStage.setScene(editPartialDebt);
        editPartialDebtCtrl.display();
    }

}