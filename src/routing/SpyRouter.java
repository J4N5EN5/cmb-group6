package routing;

import core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Router module mimicking the game-of-life behavior
 */
public class SpyRouter extends ActiveRouter {

    /**
     * Neighboring message count -setting id ({@value}). Two comma
     * separated values: min and max. Only if the amount of connected nodes
     * with the given message is between the min and max value, the message
     * is accepted for transfer and kept in the buffer.
     */
    public static final String NM_COUNT_S = "nmcount";
    private int countRange[];
    private static boolean emergencyAgent = true;
    private String currentlySendingMessageType;

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     * @param s The settings object
     */
    public SpyRouter(Settings s) {
        super(s);
        countRange = s.getCsvInts(NM_COUNT_S, 2);
    }

    /**
     * Copy constructor.
     * @param r The router prototype where setting values are copied from
     */
    protected SpyRouter(SpyRouter r) {
        super(r);
        this.countRange = r.countRange;
    }

    @Override
    public void init(DTNHost host, List<MessageListener> mListeners){
        super.init(host, mListeners);
        String initialMessageType;
        if(emergencyAgent){
            initialMessageType = "emergencySignal";
            emergencyAgent = false;
        } else {
            initialMessageType = "informationTransfer";
        }
        this.currentlySendingMessageType = initialMessageType;
        Message message = new Message(this.getHost(), this.getHost(), currentlySendingMessageType + "_" + this.getHost().getAddress(), 100);
        this.createNewMessage(message);
    }

    /**
     * Counts how many of the connected peers have the given message
     * @return Amount of connected peers with the message
     */
    private String getDominantId() {
        DTNHost me = getHost();
        int informationTransferCount = 0;
        int emergencySignalCount = 0;
        int disruptionSignalCount = 0;
        String dominantId = "None";

        for (Connection c : getConnections()) {
            if (c.getOtherNode(me).getRouter().hasMessage("informationTransfer_" + c.getOtherNode(me).getAddress())) {
                informationTransferCount++;
            } else if(c.getOtherNode(me).getRouter().hasMessage("emergencySignal_" + c.getOtherNode(me).getAddress())) {
                emergencySignalCount++;
            } else if(c.getOtherNode(me).getRouter().hasMessage("disruptionSignal_" + c.getOtherNode(me).getAddress())) {
                disruptionSignalCount++;
            }

        }
        if (informationTransferCount > 0 && informationTransferCount + emergencySignalCount > 2 && disruptionSignalCount < 5){
            dominantId = "emergencySignal";
        } else if (disruptionSignalCount >  2*emergencySignalCount + informationTransferCount) {
            dominantId = "disruptionSignal";
        } else {
            dominantId = "informationTransfer";
        }
        return dominantId;
    }

    @Override
    protected int checkReceiving(Message m, DTNHost from) {
        String dominantId = getDominantId();
        System.out.println(dominantId);

        if (!from.getRouter().hasMessage(dominantId + "_" + from.getAddress())) {
            return DENIED_POLICY;
        }

        /* peer message count check OK; receive based on other checks */
        return super.checkReceiving(m, from);
    }

    @Override
    public Message messageTransferred(String id, DTNHost from) {
        if(id.startsWith("disruptionSignal")){
            Message message = new Message(this.getHost(), this.getHost(), "disruptionSignal_" + this.getHost().getAddress(), 100);
            this.createNewMessage(message);
        }
        return super.messageTransferred(id, from);

    }

    @Override
    public void update() {
        super.update();

        if (isTransferring() || !canStartTransfer()) {
            return; /* transferring, don't try other connections yet */
        }

        this.sendSelectiveMessages();
    }

    protected Connection sendSelectiveMessages(){
        List<Connection> connections = getConnections();
        if (connections.size() == 0 || this.getNrofMessages() == 0) {
            return null;
        }
        List<Message> messages =
                new ArrayList<Message>();
        if(currentlySendingMessageType.equals("disruptionSignal")){
            messages.add(this.getMessage("disruptionSignal_" + this.getHost().getAddress()));
        } else {
            for (Message message : this.getMessageCollection()) {
                if (message.getId().startsWith(currentlySendingMessageType)) {
                    messages.add(message);
                }
            }
            this.sortByQueueMode(messages);
        }
        return tryMessagesToConnections(messages, connections);
    }


    @Override
    public SpyRouter replicate() {
        return new SpyRouter(this);
    }

}
