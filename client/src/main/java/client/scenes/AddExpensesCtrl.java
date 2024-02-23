package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class AddExpensesCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @Inject
    public AddExpensesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    public void back() {
        mainCtrl.goToOverview();
    }
}

