package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;

public class SettleDebtsCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Event event;

    /**
     * constructor for settle debts ctrl
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SettleDebtsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * back to the balances page
     */
    public void back(){
        mainCtrl.goToBalances(event);
    }

    /**
     * setter for the event
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}
