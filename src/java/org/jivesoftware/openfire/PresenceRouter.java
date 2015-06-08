package org.jivesoftware.openfire;

import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.openfire.handler.PresenceSubscribeHandler;
import org.jivesoftware.openfire.handler.PresenceUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Route presence packets throughout the server.</p>
 * <p>Routing is based on the recipient and sender addresses. The typical
 * packet will often be routed twice, once from the sender to some internal
 * server component for handling or processing, and then back to the router
 * to be delivered to it's final destination.</p>
 *
 * @author Iain Shigeoka
 */
public class PresenceRouter extends BasicModule {
	
	private static final Logger Log = LoggerFactory.getLogger(PresenceRouter.class);
	private RoutingTable routingTable;
	private PresenceUpdateHandler updateHandler;
	private PresenceSubscribeHandler subscribeHandler;
	private PresenceManager presenceManager;
    private SessionManager sessionManager;
    private EntityCapabilitiesManager entityCapsManager;
    private MulticastRouter multicastRouter;
    private String serverName;

}
