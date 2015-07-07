package org.jivesoftware.openfire.muc.spi;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.disco.DiscoInfoProvider;
import org.jivesoftware.openfire.disco.DiscoItemsProvider;
import org.jivesoftware.openfire.disco.ServerItemsProvider;
import org.jivesoftware.openfire.muc.HistoryStrategy;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.util.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.Component;
import org.xmpp.packet.JID;

/**
 * Implements the chat server as a cached memory resident chat server. The server is also
 * responsible for responding Multi-User Chat disco requests as well as removing inactive users from
 * the rooms after a period of time and to maintain a log of the conversation in the rooms that
 * require to log their conversations. The conversations log is saved to the database using a
 * separate process.
 * <p>
 * Temporary rooms are held in memory as long as they have occupants. They will be destroyed after
 * the last occupant left the room. On the other hand, persistent rooms are always present in memory
 * even after the last occupant left the room. In order to keep memory clean of persistent rooms that
 * have been forgotten or abandoned this class includes a clean up process. The clean up process
 * will remove from memory rooms that haven't had occupants for a while. Moreover, forgotten or
 * abandoned rooms won't be loaded into memory when the Multi-User Chat service starts up.</p>
 *
 * @author Gaston Dombiak
 */
public class MultiUserChatServiceImpl implements Component,
		MultiUserChatService, ServerItemsProvider, DiscoInfoProvider,
		DiscoItemsProvider {
	
	private static final Logger Log = LoggerFactory.getLogger(MultiUserChatServiceImpl.class);
	
	/**
     * The time to elapse between clearing of idle chat users.
     */
    private int user_timeout = 300000;
    /**
     * The number of milliseconds a user must be idle before he/she gets kicked from all the rooms.
     */
    private int user_idle = -1;
    /**
     * Task that kicks idle users from the rooms.
     */
    private UserTimeoutTask userTimeoutTask;
    /**
     * The time to elapse between logging the room conversations.
     */
    private int log_timeout = 300000;
    /**
     * The number of messages to log on each run of the logging process.
     */
    private int log_batch_size = 50;
    /**
     * Task that flushes room conversation logs to the database.
     */
    private LogConversationTask logConversationTask;
    /**
     * the chat service's hostname (subdomain)
     */
    private final String chatServiceName;
    /**
     * the chat service's description
     */
    private String chatDescription = null;
    
    /**
     * chatrooms managed by this manager, table: key room name (String); value ChatRoom
     */
    private Map<String, LocalMUCRoom> rooms = new ConcurrentHashMap<String, LocalMUCRoom>();
    
    /**
     * Chat users managed by this manager. This includes only users connected to this JVM.
     * That means that when running inside of a cluster each node will have its own manager
     * that in turn will keep its own list of locally connected.
     *
     * table: key user jid (XMPPAddress); value ChatUser
     */
    private Map<JID, LocalMUCUser> users = new ConcurrentHashMap<JID, LocalMUCUser>();
    private HistoryStrategy historyStrategy;
    
    /**
     * The packet router for the server.
     */
    private RoutingTable routingTable = null;
    /**
     * The handler of packets with namespace jabber:iq:register for the server.
     */
    
    
    
    private class UserTimeoutTask extends TimerTask {
    	/**
         * Remove any user that has been idle for longer than the user timeout time.
         */
		@Override
		public void run() {
			checkForTimedOutUsers();
		}
    	
    }

	private void checkForTimedOutUsers() {
		final long deadline = System.currentTimeMillis() - user_idle;
	}
	
	/**
     * Logs the conversation of the rooms that have this feature enabled.
     */
	private class LogConversationTask extends TimerTask {

		@Override
		public void run() {
			try {
                logConversation();
            }
            catch (Throwable e) {
                Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
            }
		}
		
	}

	private void logConversation() {
		ConversationLogEntry entry;
		boolean success;
        for (int index = 0; index <= log_batch_size && !logQueue.isEmpty(); index++) {
            entry = logQueue.poll();
            if (entry != null) {
                success = MUCPersistenceManager.saveConversationLogEntry(entry);
                if (!success) {
                    logQueue.add(entry);
                }
            }
        }
	}

}
