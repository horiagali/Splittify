package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class PageCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     * 
     * @param server
     * @param mainCtrl
     */
    @Inject
    public PageCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    /**
     * 
     */
    public void okay() {
        mainCtrl.showOverview();
    }

}
