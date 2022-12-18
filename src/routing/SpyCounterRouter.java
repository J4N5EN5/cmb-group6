package routing;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;

import java.util.List;

public class SpyCounterRouter extends ActiveRouter{

    /**
     * Neighboring message count -setting id ({@value}). Two comma
     * separated values: min and max. Only if the amount of connected nodes
     * with the given message is between the min and max value, the message
     * is accepted for transfer and kept in the buffer.
     */
    public static final String NM_COUNT_S = "nmcount";
    private static boolean emergencyAgent = true;

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     * @param s The settings object
     */
    public SpyCounterRouter(Settings s) {
        super(s);
    }

    /**
     * Copy constructor.
     * @param r The router prototype where setting values are copied from
     */
    protected SpyCounterRouter(SpyCounterRouter r) {
        super(r);
    }

    @Override
    public void init(DTNHost host, List<MessageListener> mListeners){
        super.init(host, mListeners);
        this.createNewMessage(new Message(this.getHost(), this.getHost(), "disruptionSignal_" + this.getHost().getAddress(), 100));
    }


    @Override
    protected int checkReceiving(Message m, DTNHost from) {return DENIED_POLICY;}

    @Override
    public void update() {
        super.update();
        if (isTransferring() || !canStartTransfer()) {
            return; /* transferring, don't try other connections yet */
        }
        this.tryAllMessagesToAllConnections();
    }


    @Override
    public SpyCounterRouter replicate() {
        return new SpyCounterRouter(this);
    }
}
