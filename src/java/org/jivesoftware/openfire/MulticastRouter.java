package org.jivesoftware.openfire;

import org.jivesoftware.openfire.container.BasicModule;
import org.xmpp.component.IQResultListener;

/**
 * Router of packets with multiple recipients. Clients may send a single packet with multiple
 * recipients and the server will broadcast the packet to the target receipients. If recipients
 * belong to remote servers, then this server will discover if remote target servers support
 * multicast service. If a remote server supports the multicast service, a single packet will be
 * sent to the remote server. If a remote server doesn't the support multicast
 * processing, the local server sends a copy of the original stanza to each address.<p>
 *
 * The current implementation will only search up to the first level of nodes of remote servers
 * when trying to find out if remote servers have support for multicast service. It is assumed
 * that it is highly unlikely for servers to have a node in the second or third depth level
 * providing the multicast service. Servers should normally provide this service themselves or
 * at least as a first level node.
 *
 * This is an implementation of <a href=http://www.jabber.org/jeps/jep-0033.html>
 * JEP-0033: Extended Stanza Addressing</a>
 *
 * @author Matt Tucker
 */
public class MulticastRouter extends BasicModule implements ServerFeaturesProvider, IQResultListener {

}
