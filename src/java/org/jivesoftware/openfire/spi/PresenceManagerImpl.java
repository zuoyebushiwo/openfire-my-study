package org.jivesoftware.openfire.spi;

import java.util.Collection;
import java.util.Map;

import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServerListener;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.openfire.event.UserEventListener;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

/**
 * Simple in memory implementation of the PresenceManager interface.
 *
 * @author Iain Shigeoka
 */
public class PresenceManagerImpl extends BasicModule implements PresenceManager, UserEventListener, XMPPServerListener {
	
	private static final String LOAD_OFFLINE_PRESENCE =
            "SELECT offlinePresence, offlineDate FROM ofPresence WHERE username=?";
    private static final String INSERT_OFFLINE_PRESENCE =
            "INSERT INTO ofPresence(username, offlinePresence, offlineDate) VALUES(?,?,?)";
    private static final String DELETE_OFFLINE_PRESENCE =
            "DELETE FROM ofPresence WHERE username=?";
    
    private static final String NULL_STRING = "NULL";
    private static final long NULL_LONG = -1L;

    private RoutingTable routingTable;
    private SessionManager sessionManager;
    private UserManager userManager;
    private RosterManager rosterManager;

	@Override
	public void serverStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverStopping() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userCreated(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userDeleting(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userModified(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAvailable(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Presence getPresence(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Presence> getPresences(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void probePresence(JID prober, JID probee) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleProbe(Presence packet) throws UnauthorizedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canProbePresence(JID prober, String probee)
			throws UserNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendUnavailableFromSessions(JID recipientJID, JID userJID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userAvailable(Presence presence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userUnavailable(Presence presence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLastPresenceStatus(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLastActivity(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

}
