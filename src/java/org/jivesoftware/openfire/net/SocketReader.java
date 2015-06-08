package org.jivesoftware.openfire.net;

import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.session.LocalSession;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * A SocketReader creates the appropriate {@link Session} based on the defined namespace in the
 * stream element and will then keep reading and routing the received packets.
 *
 * @author Gaston Dombiak
 */
public abstract class SocketReader implements Runnable {
	
	private static final Logger Log = LoggerFactory.getLogger(SocketReader.class);

    /**
     * The utf-8 charset for decoding and encoding Jabber packet streams.
     */
    private static String CHARSET = "UTF-8";
    /**
     * Reuse the same factory for all the connections.
     */
    private static XmlPullParserFactory factory = null;
    
    /**
     * Session associated with the socket reader.
     */
    protected LocalSession session;
    /**
     * Reference to the physical connection.
     */
    protected SocketConnection connection;
    /**
     * Server name for which we are attending clients.
     */
    protected String serverName;
    
    /**
     * Router used to route incoming packets to the correct channels.
     */
    private PacketRouter router;

}
