package org.jivesoftware.openfire.spi;

import java.util.Collection;
import java.util.Set;

import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.cluster.ClusterEventListener;
import org.jivesoftware.openfire.cluster.NodeID;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.util.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/**
 * Routing table that stores routes to client sessions, outgoing server sessions
 * and components. As soon as a user authenticates with the server its client session
 * will be added to the routing table. Whenever the client session becomes available
 * or unavailable the routing table will be updated too.<p>
 *
 * When running inside of a cluster the routing table will also keep references to routes
 * hosted in other cluster nodes. A {@link RemotePacketRouter} will be use to route packets
 * to routes hosted in other cluster nodes.<p>
 *
 * Failure to route a packet will end up sending {@link IQRouter#routingFailed(JID, Packet)},
 * {@link MessageRouter#routingFailed(JID, Packet)} or {@link PresenceRouter#routingFailed(JID, Packet)}
 * depending on the packet type that tried to be sent.
 *
 * @author Gaston Dombiak
 */
public class RoutingTableImpl extends BasicModule implements RoutingTable, ClusterEventListener {
	
	private static final Logger Log = LoggerFactory.getLogger(RoutingTableImpl.class);
	
    public static final String C2S_CACHE_NAME = "Routing Users Cache";
    public static final String ANONYMOUS_C2S_CACHE_NAME = "Routing AnonymousUsers Cache";
    public static final String S2S_CACHE_NAME = "Routing Servers Cache";
    public static final String COMPONENT_CACHE_NAME = "Routing Components Cache";
    public static final String C2S_SESSION_NAME = "Routing User Sessions";
    
    /**
     * Cache (unlimited, never expire) that holds outgoing sessions to remote servers from this server.
     * Key: server domain, Value: nodeID
     */
    private Cache<String, byte[]> serversCache;
    /**
     * Cache (unlimited, never expire) that holds components connected to the server.
     * Key: component domain, Value: list of nodeIDs hosting the component
     */
    private Cache<String, Set<NodeID>> componentsCache;
    /**
     * Cache (unlimited, never expire) that holds sessions of user that have authenticated with the server.
     * Key: full JID, Value: {nodeID, available/unavailable}
     */
    private Cache<String, ClientRoute> usersCache;
    /**
     * Cache (unlimited, never expire) that holds sessions of anonymous user that have authenticated with the server.
     * Key: full JID, Value: {nodeID, available/unavailable}
     */
    private Cache<String, ClientRoute> anonymousUsersCache;
    /**
     * Cache (unlimited, never expire) that holds list of connected resources of authenticated users
     * (includes anonymous).
     * Key: bare JID, Value: list of full JIDs of the user
     */
    private Cache<String, Collection<String>> usersSessions;
    
    private String serverName;
    private XMPPServer server;
    private LocalRoutingTable localRoutingTable;
    private RemotePacketRouter remotePacketRouter;
    private IQRouter iqRouter;
    private MessageRouter messageRouter;
    private PresenceRouter presenceRouter;
    private PresenceUpdateHandler presenceUpdateHandler;

}
