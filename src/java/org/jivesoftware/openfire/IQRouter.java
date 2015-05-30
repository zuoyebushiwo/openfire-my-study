package org.jivesoftware.openfire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Element;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.TaskEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.IQResultListener;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

/**
 * Routes iq packets throughout the server. Routing is based on the recipient
 * and sender addresses. The typical packet will often be routed twice, once
 * from the sender to some internal server component for handling or processing,
 * and then back to the router to be delivered to it's final destination.
 *
 * @author Iain Shigeoka
 */
public class IQRouter extends BasicModule {
	
	private static final Logger Log = LoggerFactory.getLogger(IQRouter.class);
	
	private RoutingTable routingTable;
	private MulticastRouter multicastRouter;
	private String serverName;
    private List<IQHandler> iqHandlers = new ArrayList<IQHandler>();
    private Map<String, IQHandler> namespace2Handlers = new ConcurrentHashMap<String, IQHandler>();
    private Map<String, IQResultListener> resultListeners = new ConcurrentHashMap<String, IQResultListener>();
    private Map<String, Long> resultTimeout = new ConcurrentHashMap<String, Long>();
    private SessionManager sessionManager;
    private UserManager userManager;
    
	/**
     * Creates a packet router.
     */
    public IQRouter() {
        super("XMPP IQ Router");
    }
    
    /**
     * <p>Performs the actual packet routing.</p>
     * <p>You routing is considered 'quick' and implementations may not take
     * excessive amounts of time to complete the routing. If routing will take
     * a long amount of time, the actual routing should be done in another thread
     * so this method returns quickly.</p>
     * <h2>Warning</h2>
     * <p>Be careful to enforce concurrency DbC of concurrent by synchronizing
     * any accesses to class resources.</p>
     *
     * @param packet The packet to route
     * @throws NullPointerException If the packet is null
     */
    public void route(IQ packet) {
    	if (packet == null) {
            throw new NullPointerException();
        }
    	JID sender = packet.getFrom();
    	ClientSession session = sessionManager.getSession(sender);
    	try {
    		// Invoke the interceptors before we process the read packet
    		InterceptorManager.getInstance().invokeInterceptors(packet, session, true, false);
            JID to = packet.getTo();
            
            if (session != null && to != null && session.getStatus() == Session.STATUS_CONNECTED &&
                    !serverName.equals(to.toString())) {
                // User is requesting this server to authenticate for another server. Return
                // a bad-request error
                IQ reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.bad_request);
                session.process(reply);
                Log.warn("User tried to authenticate with this server using an unknown receipient: " +
                        packet.toXML());
            }
            else if (session == null || session.getStatus() == Session.STATUS_AUTHENTICATED || (
                    isLocalServer(to) && (
                            "jabber:iq:auth".equals(packet.getChildElement().getNamespaceURI()) ||
                                    "jabber:iq:register"
                                            .equals(packet.getChildElement().getNamespaceURI()) ||
                                    "urn:ietf:params:xml:ns:xmpp-bind"
                                            .equals(packet.getChildElement().getNamespaceURI())))) {
                handle(packet);
            }
            else {
                IQ reply = IQ.createResultIQ(packet);
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setError(PacketError.Condition.not_authorized);
                session.process(reply);
            }
            // Invoke the interceptors after we have processed the read packet
            InterceptorManager.getInstance().invokeInterceptors(packet, session, true, true);
		} catch (PacketRejectedException e) {
            if (session != null) {
                // An interceptor rejected this packet so answer a not_allowed error
                IQ reply = new IQ();
                reply.setChildElement(packet.getChildElement().createCopy());
                reply.setID(packet.getID());
                reply.setTo(session.getAddress());
                reply.setFrom(packet.getTo());
                reply.setError(PacketError.Condition.not_allowed);
                session.process(reply);
                // Check if a message notifying the rejection should be sent
                if (e.getRejectionMessage() != null && e.getRejectionMessage().trim().length() > 0) {
                    // A message for the rejection will be sent to the sender of the rejected packet
                    Message notification = new Message();
                    notification.setTo(session.getAddress());
                    notification.setFrom(packet.getTo());
                    notification.setBody(e.getRejectionMessage());
                    session.process(notification);
                }
            }
        }
    }
    
    /**
     * <p>Adds a new IQHandler to the list of registered handler. The new IQHandler will be
     * responsible for handling IQ packet whose namespace matches the namespace of the
     * IQHandler.</p>
     *
     * An IllegalArgumentException may be thrown if the IQHandler to register was already provided
     * by the server. The server provides a certain list of IQHandlers when the server is
     * started up.
     *
     * @param handler the IQHandler to add to the list of registered handler.
     */
    public void addHandler(IQHandler handler) {
    	if (iqHandlers.contains(handler)) {
            throw new IllegalArgumentException("IQHandler already provided by the server");
        }
        // Ask the handler to be initialized
        handler.initialize(XMPPServer.getInstance());
        // Register the handler as the handler of the namespace
        namespace2Handlers.put(handler.getInfo().getNamespace(), handler);
    }
    
    /**
     * <p>Removes an IQHandler from the list of registered handler. The IQHandler to remove was
     * responsible for handling IQ packet whose namespace matches the namespace of the
     * IQHandler.</p>
     *
     * An IllegalArgumentException may be thrown if the IQHandler to remove was already provided
     * by the server. The server provides a certain list of IQHandlers when the server is
     * started up.
     *
     * @param handler the IQHandler to remove from the list of registered handler.
     */
    public void removeHandler(IQHandler handler) {
        if (iqHandlers.contains(handler)) {
            throw new IllegalArgumentException("Cannot remove an IQHandler provided by the server");
        }
        // Unregister the handler as the handler of the namespace
        namespace2Handlers.remove(handler.getInfo().getNamespace());
    }
    
    /**
	 * Adds an {@link IQResultListener} that will be invoked when an IQ result
	 * is sent to the server itself and is of type result or error. This is a
	 * nice way for the server to send IQ packets to other XMPP entities and be
	 * waked up when a response is received back.<p>
     *
	 * Once an IQ result was received, the listener will be invoked and removed
	 * from the list of listeners.<p>
	 * 
	 * If no result was received within one minute, the timeout method of the
	 * listener will be invoked and the listener will be removed from the list
	 * of listeners.
     *
	 * @param id
	 *            the id of the IQ packet being sent from the server to an XMPP
	 *            entity.
	 * @param listener
	 *            the IQResultListener that will be invoked when an answer is
	 *            received
     */
    public void addIQResultListener(String id, IQResultListener listener) {
        addIQResultListener(id, listener, 60 * 1000);
    }

    /**
	 * Adds an {@link IQResultListener} that will be invoked when an IQ result
	 * is sent to the server itself and is of type result or error. This is a
	 * nice way for the server to send IQ packets to other XMPP entities and be
	 * waked up when a response is received back.<p>
	 * 
	 * Once an IQ result was received, the listener will be invoked and removed
	 * from the list of listeners.<p>
	 * 
	 * If no result was received within the specified amount of milliseconds,
	 * the timeout method of the listener will be invoked and the listener will
	 * be removed from the list of listeners.<p>
	 * 
	 * Note that the listener will remain active for <em>at least</em> the
	 * specified timeout value. The listener will not be removed at the exact
	 * moment it times out. Instead, purging of timed out listeners is a
	 * periodic scheduled job.
	 * 
	 * @param id
	 *            the id of the IQ packet being sent from the server to an XMPP
	 *            entity.
	 * @param listener
	 *            the IQResultListener that will be invoked when an answer is
	 *            received.
	 * @param timeoutmillis
	 *            The amount of milliseconds after which waiting for a response
	 *            should be stopped.
	 */    
    public void addIQResultListener(String id, IQResultListener listener, long timeoutmillis) {
        resultListeners.put(id, listener);
        resultTimeout.put(id, System.currentTimeMillis() + timeoutmillis);
    }
    
    @Override
	public void initialize(XMPPServer server) {
        super.initialize(server);
        TaskEngine.getInstance().scheduleAtFixedRate(new TimeoutTask(), 5000, 5000);
        serverName = server.getServerInfo().getXMPPDomain();
        routingTable = server.getRoutingTable();
        multicastRouter = server.getMulticastRouter();
        iqHandlers.addAll(server.getIQHandlers());
        sessionManager = server.getSessionManager();
        userManager = server.getUserManager();
    }
    
    /**
     * A JID is considered local if:
     * 1) is null or
     * 2) has no domain or domain is empty
     * or
     * if it's not a full JID and it was sent to the server itself.
     *
     * @param recipientJID address to check.
     * @return true if the specified address belongs to the local server.
     */
    private boolean isLocalServer(JID recipientJID) {
        // Check if no address was specified in the IQ packet
        boolean implicitServer =
                recipientJID == null || recipientJID.getDomain() == null || "".equals(recipientJID.getDomain());
        if (!implicitServer) {
            // We found an address. Now check if it's a bare or full JID
            if (recipientJID.getNode() == null || recipientJID.getResource() == null) {
                // Address is a bare JID so check if it was sent to the server itself
                return serverName.equals(recipientJID.getDomain());
            }
            // Address is a full JID. IQ packets sent to full JIDs are not handle by the server
            return false;
        }
        return true;
    }
    
    private void handle(IQ packet) {
    	JID recipientJID = packet.getTo();
    	// Check if the packet was sent to the server hostname
    	if (recipientJID != null && recipientJID.getNode() == null &&
                recipientJID.getResource() == null && serverName.equals(recipientJID.getDomain())) {
            Element childElement = packet.getChildElement();
            if (childElement != null && childElement.element("addresses") != null) {
                // Packet includes multicast processing instructions. Ask the multicastRouter
                // to route this packet
                multicastRouter.route(packet);
                return;
            }
        }
	}
    
    /**
	 * Timer task that will remove Listeners that wait for results to IQ stanzas
	 * that have timed out. Time out values can be set to each listener
	 * individually by adjusting the timeout value in the third parameter of
	 * {@link IQRouter#addIQResultListener(String, IQResultListener, long)}.
	 * 
	 * @author Guus der Kinderen, guus@nimbuzz.com
	 */
    private class TimeoutTask extends TimerTask {

        /**
         * Iterates over and removes all timed out results.<p>
         * 
         * The map that keeps track of timeout values is ordered by timeout
         * date. This way, iteration can be stopped as soon as the first value
         * has been found that didn't timeout yet.
         */
        @Override
        public void run() {
            // Use an Iterator to allow changes to the Map that is backing
            // the Iterator.
            final Iterator<Map.Entry<String, Long>> it = resultTimeout.entrySet().iterator();

            while (it.hasNext()) {
                final Map.Entry<String, Long> pointer = it.next();

                if (System.currentTimeMillis() < pointer.getValue()) {
                    // This entry has not expired yet. Ignore it.
                    continue;
                }

                final String packetId = pointer.getKey();

                // remove this listener from the list
                final IQResultListener listener = resultListeners.remove(packetId);
                if (listener != null) {
                    // notify listener of the timeout.
                    listener.answerTimeout(packetId);
                }

                // remove the packet from the list that's used to track
                // timeouts
                it.remove();
            }
        }
	}

}
