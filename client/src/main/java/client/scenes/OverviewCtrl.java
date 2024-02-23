package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class OverviewCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }
    public void back() {
        mainCtrl.showOverview();
    }
    public void addExpense(){
        mainCtrl.showAddExpenses();
    }
    public void goToContact(){
        mainCtrl.goToContact();
    }

}
