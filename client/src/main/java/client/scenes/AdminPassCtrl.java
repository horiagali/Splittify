package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class AdminPassCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public AdminPassCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    /**
     *
     */
    public void goBack() {
        mainCtrl.showOverview();
    }

}
