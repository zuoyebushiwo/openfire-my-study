package org.jivesoftware.openfire.transport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.openfire.Channel;
import org.jivesoftware.openfire.ChannelHandler;
import org.jivesoftware.openfire.PacketDeliverer;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.util.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

/**
 * Routes packets to the appropriate transport gateway or drops the packet.
 *
 * @author Iain Shigeoka
 */
public class TransportHandler extends BasicModule implements ChannelHandler {
	
	private static final Logger Log = LoggerFactory.getLogger(TransportHandler.class);
	
	private Map<String, Channel> transports = new ConcurrentHashMap<String, Channel>();
	
	private PacketDeliverer deliverer;

	public TransportHandler() {
		super("Transport handler");
	}

	public void addTransport(Channel transport) {
        transports.put(transport.getName(), transport);
    }
	
	@Override
	public void process(Packet packet) throws UnauthorizedException,
			PacketException {
		boolean handled = false;
		String host = packet.getTo().getDomain();
		for (Channel channel : transports.values()) {
			if (channel.getName().equalsIgnoreCase(host)) {
                channel.add(packet);
                handled = true;
            }
		}
		
		if (!handled) {
            JID recipient = packet.getTo();
            JID sender = packet.getFrom();
            packet.setError(PacketError.Condition.remote_server_timeout);
            packet.setFrom(recipient);
            packet.setTo(sender);
            try {
                deliverer.deliver(packet);
            }
            catch (PacketException e) {
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
        }
	}
	
	@Override
	public void initialize(XMPPServer server) {
        super.initialize(server);
        deliverer = server.getPacketDeliverer();
    }

}
