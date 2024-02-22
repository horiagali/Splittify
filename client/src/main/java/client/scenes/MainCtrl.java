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

import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private StartPageCtrl startPageCtrl;
    private Scene startPage;

    private AddExpensesCtrl addExpensesCtrl;
    private Scene addExpenses;

    private Scene contactDetails;
    private ContactDetailsCtrl contactDetailsCtrl;

    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> overview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<PageCtrl, Parent> page,
                           Pair<StartPageCtrl, Parent> startPage, Pair<AddExpensesCtrl, Parent> addExpense,
                           Pair<ContactDetailsCtrl, Parent> contactDetails) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.pageCtrl = page.getKey();
        this.page = new Scene(page.getValue());

        this.startPageCtrl = startPage.getKey();
        this.startPage = new Scene(startPage.getValue());

        this.addExpensesCtrl = addExpense.getKey();
        this.addExpenses = new Scene(addExpense.getValue());

        this.contactDetailsCtrl = contactDetails.getKey();
        this.contactDetails = new Scene(contactDetails.getValue());

        showOverview();
        primaryStage.show();
    }

    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    public void showPage() {
        primaryStage.setTitle("Iulia's Page");
        primaryStage.setScene(page);
    }

    public void startPage() {
        primaryStage.setTitle("Start Screen");
        primaryStage.setScene(startPage);
    }

    public void showAddExpenses() {
        primaryStage.setTitle("Expenses: Add Expense");
        primaryStage.setScene(addExpenses);
        // Additional setup for the Add Expense page, if needed
    }

    public void goToContact() {
        primaryStage.setTitle("Contact Details");
        primaryStage.setScene(contactDetails);
    }
}