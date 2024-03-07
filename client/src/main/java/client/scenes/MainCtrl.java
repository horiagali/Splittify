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

import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private PageCtrl pageCtrl;
    private Scene page;


    private AddExpensesCtrl addExpensesCtrl;
    private Scene addExpenses;

    private Scene contactDetails;
    private ContactDetailsCtrl contactDetailsCtrl;

    private Scene overviewApp;
    private OverviewCtrl overviewAppCtrl;

    private Scene invite;
    private InviteCtrl inviteCtrl;

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
     */

    @SuppressWarnings({"ParameterNumber"})
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<PageCtrl, Parent> page,
                           Pair<AddExpensesCtrl, Parent> addExpense,
                           Pair<ContactDetailsCtrl, Parent> contactDetails, 
                           Pair<OverviewCtrl, Parent> overviewApp,
                            Pair<InviteCtrl, Parent> invite) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.pageCtrl = page.getKey();
        this.page = new Scene(page.getValue());


        this.addExpensesCtrl = addExpense.getKey();
        this.addExpenses = new Scene(addExpense.getValue());

        this.contactDetailsCtrl = contactDetails.getKey();
        this.contactDetails = new Scene(contactDetails.getValue());

        this.overviewAppCtrl = overviewApp.getKey();
        this.overviewApp = new Scene(overviewApp.getValue());

        this.inviteCtrl = invite.getKey();
        this.invite = new Scene(invite.getValue());
        showOverview();
        primaryStage.show();
    }

    /**
     * 
     */
    public void showOverview() {
        primaryStage.setTitle("Events: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
//        startPageCtrl.refresh();  doesn't work (yet!)
    }

    /**
     * 
     * @param name
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
     * 
     */
    public void showPage() {
        primaryStage.setTitle("Iulia's Page");
        primaryStage.setScene(page);
    }

    /**
     * 
     */

    /**
     * 
     */
    public void showAddExpenses() {
        primaryStage.setTitle("Expenses: Add Expense");
        primaryStage.setScene(addExpenses);
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
    }

    /**
     * 
     * @param eventName
     */
    public void sendInvites(Label eventName) {
        primaryStage.setTitle("Send Invites");
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
        overviewCtrl.refresh();

    }
}