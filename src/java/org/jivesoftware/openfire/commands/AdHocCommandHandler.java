package org.jivesoftware.openfire.commands;

import java.util.Iterator;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.disco.DiscoInfoProvider;
import org.jivesoftware.openfire.disco.DiscoItem;
import org.jivesoftware.openfire.disco.DiscoItemsProvider;
import org.jivesoftware.openfire.disco.IQDiscoInfoHandler;
import org.jivesoftware.openfire.disco.ServerFeaturesProvider;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.forms.DataForm;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

/**
 * An AdHocCommandHandler is responsbile for providing discoverable information
 * about the supported commands and for handling commands requests. This is an
 * implementation of JEP-50: Ad-Hoc Commands.
 * <p>
 *
 * Ad-hoc commands that require user interaction will have one or more stages.
 * For each stage the user will complete a data form and send it back to the
 * server. The data entered by the user is kept in a SessionData. Instances of
 * {@link AdHocCommand} are stateless. In order to prevent "bad" users from
 * consuming all system memory there exists a limit of simultaneous commands
 * that a user might perform. Configure the system property
 * <tt>"xmpp.command.limit"</tt> to control this limit. User sessions will also
 * timeout and their data destroyed if they have not been executed within a time
 * limit since the session was created. The default timeout value is 10 minutes.
 * The timeout value can be modified by setting the system property
 * <tt>"xmpp.command.timeout"</tt>.
 * <p>
 *
 * New commands can be added dynamically by sending the message
 * {@link #addCommand(AdHocCommand)}. The command will immediatelly appear in
 * the disco#items list and might be executed by those users with enough
 * execution permissions.
 *
 * @author Gaston Dombiak
 */
public class AdHocCommandHandler extends IQHandler implements
		ServerFeaturesProvider, DiscoInfoProvider, DiscoItemsProvider {

	private static final String NAMESPACE = "http://jabber.org/protocol/commands";
	private IQHandlerInfo info;
	private IQDiscoInfoHandler infoHandler;
    private IQDiscoItemsHandler itemsHandler;
	
	@Override
	public Iterator<DiscoItem> getItems(String name, String node, JID senderJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Element> getIdentities(String name, String node,
			JID senderJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> getFeatures(String name, String node, JID senderJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataForm getExtendedInfo(String name, String node, JID senderJID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasInfo(String name, String node, JID senderJID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<String> getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQHandlerInfo getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
