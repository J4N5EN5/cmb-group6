package routing;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;

/**
 * Epidemic message router with drop-oldest buffer and only single transferring
 * connections at a time.
 */
public class MyEpidemicRouter extends ActiveRouter {

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s The settings object
     */
    public MyEpidemicRouter(Settings s) {
        super(s);
        //TODO: read&use epidemic router specific settings (if any)
    }

    /**
     * Copy constructor.
     *
     * @param r The router prototype where setting values are copied from
     */
    protected MyEpidemicRouter(MyEpidemicRouter r) {
        super(r);
        //TODO: copy epidemic settings here (if any)
    }

    @Override
    public void update() {
        super.update();
        if (isTransferring() || !canStartTransfer()) {
            return; // transferring, don't try other connections yet
        }

        // Try first the messages that can be delivered to final recipient
        if (exchangeDeliverableMessages() != null) {
            return; // started a transfer, don't try others (yet)
        }

        // then try any/all message to any/all connection
        this.tryAllMessagesToAllConnections();
    }

    @Override
    protected int checkReceiving(Message m, DTNHost from) {
        int recvCheck = super.checkReceiving(m, from);

        if (recvCheck == RCV_OK) {
            /* don't accept a message that has already traversed this node */
            if (m.getHops().contains(getHost())) {
                recvCheck = DENIED_OLD;
            }
            if (m.getHopCount() > 0) {
                recvCheck = DENIED_POLICY;
            }
        }
        return recvCheck;
    }


    @Override
    public MyEpidemicRouter replicate() {
        return new MyEpidemicRouter(this);
    }

}