package org.jivesoftware.openfire.muc;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jivesoftware.util.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;

/**
 * <p>Multi-User Chat rooms may cache history of the conversations in the room in order to
 * play them back to newly arriving members.</p>
 * 
 * <p>This class is an internal component of MUCRoomHistory that describes the strategy that can 
 * be used, and provides a method of administering the history behavior.</p>
 *
 * @author Gaston Dombiak
 * @author Derek DeMoro
 */
public class HistoryStrategy {

	private static final Logger Log = LoggerFactory.getLogger(HistoryStrategy.class);
	
	/**
     * The type of strategy being used.
     */
    private Type type = Type.number;
    
    /**
     * List containing the history of messages.
     */
    private ConcurrentLinkedQueue<Message> history = new ConcurrentLinkedQueue<Message>();
    
    /**
     * Default max number.
     */
    private static final int DEFAULT_MAX_NUMBER = 25;
    
    /**
     * The maximum number of chat history messages stored for the room.
     */
    private int maxNumber;
    
    /**
     * The parent history used for default settings, or null if no parent
     * (chat server defaults).
     */
    private HistoryStrategy parent;
    
    /**
     * Track the latest room subject change or null if none exists yet.
     */
    private Message roomSubject = null;
    
    /**
     * The string prefix to be used on the context property names
     * (do not include trailing dot).
     */
    private String contextPrefix = null;
    
    /**
     * The subdomain of the service the properties are set on.
     */
    private String contextSubdomain = null;
    
    /**
     * Create a history strategy with the given parent strategy (for defaults) or null if no 
     * parent exists.
     *
     * @param parentStrategy The parent strategy of this strategy or null if none exists.
     */
    public HistoryStrategy(HistoryStrategy parentStrategy) {
    	this.parent = parentStrategy;
    	if (parent == null) {
    		maxNumber = DEFAULT_MAX_NUMBER;
		}
    	else {
            type = Type.defaulType;
            maxNumber = parent.getMaxNumber();
        }
	}
    
    /**
     * Obtain the maximum number of messages for strategies using message number limitations.
     *
     * @return The maximum number of messages to store in applicable strategies.
     */
    public int getMaxNumber() {
        return maxNumber;
    }
    
    /**
     * Set the maximum number of messages for strategies using message number limitations.
     *
     * @param max the maximum number of messages to store in applicable strategies.
     */
    public void setMaxNumber(int max) {
        if (maxNumber == max) {
            // Do nothing since value has not changed
            return;
        }
        this.maxNumber = max;
        if (contextPrefix != null){
            MUCPersistenceManager.setProperty(contextSubdomain, contextPrefix + ".maxNumber", Integer.toString(maxNumber));
        }
        if (parent == null) {
            // Update the history strategy of the MUC service
            CacheFactory.doClusterTask(new UpdateHistoryStrategy(contextSubdomain, this));
        }
    }
    
    /**
     * Set the type of history strategy being used.
     *
     * @param newType The new type of chat history to use.
     */
    public void setType(Type newType){
        if (type == newType) {
            // Do nothing since value has not changed
            return;
        }
        if (newType != null){
            type = newType;
        }
        if (contextPrefix != null){
            MUCPersistenceManager.setProperty(contextSubdomain, contextPrefix + ".type", type.toString());
        }
        if (parent == null) {
            // Update the history strategy of the MUC service
            CacheFactory.doClusterTask(new UpdateHistoryStrategy(contextSubdomain, this));
        }
    }

    /**
     * Obtain the type of history strategy being used.
     *
     * @return The current type of strategy being used.
     */
    public Type getType(){
        return type;
    }
    
    /**
     * Add a message to the current chat history. The strategy type will determine what 
     * actually happens to the message.
     *
     * @param packet The packet to add to the chatroom's history.
     */
    public void addMessage(Message packet){
    	// get the conditions based on default or not
    	Type strategyType;
        int strategyMaxNumber;
        if (type == Type.defaulType && parent != null) {
        	strategyType = parent.getType();
            strategyMaxNumber = parent.getMaxNumber();
		}
        else {
            strategyType = type;
            strategyMaxNumber = maxNumber;
        }
        
        // Room subject change messages are special
        boolean subjectChange = false;
        if (packet.getSubject() != null && packet.getSubject().length() > 0){
            subjectChange = true;
            roomSubject = packet;
        }
        
     // store message according to active strategy
        if (strategyType == Type.none){
            if (subjectChange) {
                history.clear();
                history.add(packet);
            }
        }
        else if (strategyType == Type.all) {
            history.add(packet);
        }
        else if (strategyType == Type.number) {
            if (history.size() >= strategyMaxNumber) {
                // We have to remove messages so the new message won't exceed
                // the max history size
                // This is complicated somewhat because we must skip over the
                // last room subject
                // message because we want to preserve the room subject if
                // possible.
                Iterator<Message> historyIter = history.iterator();
                while (historyIter.hasNext() && history.size() > strategyMaxNumber) {
                    if (historyIter.next() != roomSubject) {
                        historyIter.remove();
                    }
                }
            }
            history.add(packet);
        }
    }
    
    /**
     * Obtain the current history as an iterator of messages to play back to a new room member.
     * 
     * @return An iterator of Message objects to be sent to the new room member.
     */
    public Iterator<Message> getMessageHistory(){
        LinkedList<Message> list = new LinkedList<Message>(history);
        // Sort messages. Messages may be out of order when running inside of a cluster
        Collections.sort(list, new MessageComparator());
        return list.iterator();
    }
    
    /**
     * Strategy type.
     */
    public enum Type {
        defaulType, none, all, number
    };
    
    private static class MessageComparator implements Comparator<Message> {
        public int compare(Message o1, Message o2) {
            String stamp1 = o1.getChildElement("x", "jabber:x:delay").attributeValue("stamp");
            String stamp2 = o2.getChildElement("x", "jabber:x:delay").attributeValue("stamp");
            return stamp1.compareTo(stamp2);
        }
    }
	
}
