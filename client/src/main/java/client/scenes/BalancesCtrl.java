package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

public class BalancesCtrl{
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     * constructor for the BalancesCtrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public BalancesCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

}
