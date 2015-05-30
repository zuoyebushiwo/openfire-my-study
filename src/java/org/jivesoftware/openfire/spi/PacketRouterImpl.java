package org.jivesoftware.openfire.spi;

import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.container.BasicModule;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * An uber router that can handle any packet type.<p>
 *
 * The interface is provided primarily as a convenience for services
 * that must route all packet types (e.g. s2s routing, e2e encryption, etc).
 *
 * @author Iain Shigeoka
 */
public class PacketRouterImpl extends BasicModule implements PacketRouter {
	
	private IQRouter iqRouter;
    private PresenceRouter presenceRouter;
    private MessageRouter messageRouter;

	public PacketRouterImpl(String moduleName) {
		super(moduleName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void route(Packet packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void route(IQ packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void route(Message packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void route(Presence packet) {
		// TODO Auto-generated method stub
		
	}

}
