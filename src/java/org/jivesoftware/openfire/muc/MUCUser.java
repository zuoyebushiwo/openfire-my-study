package org.jivesoftware.openfire.muc;

import org.jivesoftware.openfire.ChannelHandler;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/**
 * The chat user is a separate user abstraction for interacting with
 * the chat server. Centralizing chat users to the Jabber entity that
 * sends and receives the chat messages allows us to create quality of
 * service, authorization, and resource decisions on a real-user basis.
 * <p>
 * Most chat users in a typical s2s scenario will not be local users.
 * </p><p>
 * MUCUsers play one or more roles in one or more chat rooms on the
 * server.
 * </p>
 *
 * @author Gaston Dombiak
 */
public interface MUCUser extends ChannelHandler<Packet> {
	
	/**
     * Obtain the address of the user. The address is used by services like the core
     * server packet router to determine if a packet should be sent to the handler.
     * Handlers that are working on behalf of the server should use the generic server
     * hostname address (e.g. server.com).
     *
     * @return the address of the packet handler.
     */
	public JID getAddress();

}