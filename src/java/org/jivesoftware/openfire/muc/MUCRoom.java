package org.jivesoftware.openfire.muc;

import java.io.Externalizable;

import org.jivesoftware.database.JiveID;
import org.jivesoftware.util.JiveConstants;
import org.xmpp.packet.JID;
import org.xmpp.resultsetmanagement.Result;

/**
 * A chat room on the chat server manages its users, and
 * enforces it's own security rules.
 *
 * @author Gaston Dombiak
 */
@JiveID(JiveConstants.MUC_ROOM)
public interface MUCRoom extends Externalizable, Result {
	
	/**
     * Get the name of this room.
     *
     * @return The name for this room
     */
	String getName();
	
	/**
     * Get the full JID of this room.
     *
     * @return the JID for this room.
     */
	JID getJID();
	
	/**
     * Obtain a unique numerical id for this room. Useful for storing rooms in databases. If the 
     * room is persistent or is logging the conversation then the returned ID won't be -1.
     *
     * @return The unique id for this room or -1 if the room is temporary and is not logging the
     * conversation.
     */
    long getID();

    /**
     * Sets a new room ID if the room has just been saved to the database or sets the saved ID of
     * the room in the database while loading the room. 
     * 
     * @param roomID the saved ID of the room in the DB or a new one if the room is being saved to the DB.
     */
    void setID(long roomID);

}
