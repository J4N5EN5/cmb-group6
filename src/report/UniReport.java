/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.*;
import routing.SpyRouter;

/**
 * Report for generating different kind of total statistics about message
 * relaying performance. Messages that were created during the warm up period
 * are ignored.
 * <P><strong>Note:</strong> if some statistics could not be created (e.g.
 * overhead ratio if no messages were delivered) "NaN" is reported for
 * double values and zero for integer median(s).
 */
public class UniReport extends Report implements ConnectionListener, UpdateListener {
    private int spyMeetings;

    public UniReport() {
    }

    @Override
    public void hostsConnected(DTNHost host1, DTNHost host2) {

        if (host1.getRouter() instanceof SpyRouter &&
                host2.getRouter() instanceof SpyRouter) {
            spyMeetings ++;
        }

    }

    @Override
    public void hostsDisconnected(DTNHost host1, DTNHost host2) {

    }

    @Override
    public void updated(List<DTNHost> hosts) {

    }

    @Override
    public void done() {
        write("Spy meetings: " + spyMeetings);

        super.done();
    }
}
