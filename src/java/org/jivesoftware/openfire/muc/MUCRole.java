package org.jivesoftware.openfire.muc;

import org.xmpp.packet.Presence;

/**
 * Defines the permissions and actions that a MUCUser may use in
 * a particular room. Each MUCRole defines the relationship between
 * a MUCRoom and a MUCUser.
 * <p>
 * MUCUsers can play different roles in different chatrooms.
 * </p>
 * @author Gaston Dombiak
 */
public interface MUCRole {

	/**
     * Obtain the current presence status of a user in a chatroom.
     *
     * @return The presence of the user in the room.
     */
	public Presence getPresence();
	
	/**
     * Set the current presence status of a user in a chatroom.
     *
     * @param presence The presence of the user in the room.
     */
    public void setPresence(Presence presence);
    
    /**
     * Call this method to promote or demote a user's role in a chatroom.
     * It is common for the chatroom or other chat room members to change
     * the role of users (a moderator promoting another user to moderator
     * status for example).
     * <p>
     * Owning ChatUsers should have their membership roles updated.
     * </p>
     *
     * @param newRole The new role that the user will play.
     * @throws NotAllowedException   Thrown if trying to change the moderator role to an owner or
     *                               administrator.
     */
    public void setRole(Role newRole) throws NotAllowedException;
    
    /**
     * Obtain the role state of the user.
     *
     * @return The role status of this user.
     */
    public Role getRole();
    
    public enum Role {
    	
    	/**
         * Runs moderated discussions. Is allowed to kick users, grant and revoke voice, etc.
         */
        moderator(0),

        /**
         * A normal occupant of the room. An occupant who does not have administrative privileges; in
         * a moderated room, a participant is further defined as having voice
         */
        participant(1),
        
        /**
         * An occupant who does not have voice  (can't speak in the room)
         */
        visitor(2),
        
        /**
         * An occupant who does not permission to stay in the room (was banned)
         */
        none(3);
    	
    	private int value;

        Role(int value) {
            this.value = value;
        }

        /**
         * Returns the value for the role.
         *
         * @return the value.
         */
        public int getValue() {
            return value;
        }
        
        /**
         * Returns the affiliation associated with the specified value.
         *
         * @param value the value.
         * @return the associated affiliation.
         */
        public static Role valueOf(int value) {
            switch (value) {
                case 0: return moderator;
                case 1: return participant;
                case 2: return visitor;
                default: return none;
            }
        }
    }
    
    public enum Affiliation {
    	
    	/**
         * Owner of the room.
         */
        owner(10),

        /**
         * Administrator of the room.
         */
        admin(20),

        /**
         * A user who is on the "whitelist" for a members-only room or who is registered
         * with an open room.
         */
        member(30),

        /**
         * A user who has been banned from a room.
         */
        outcast(40),

        /**
         * A user who doesn't have an affiliation. This kind of users can register with members-only
         * rooms and may enter an open room.
         */
        none(50);

        private int value;

        Affiliation(int value) {
            this.value = value;
        }

        /**
         * Returns the value for the role.
         *
         * @return the value.
         */
        public int getValue() {
            return value;
        }

        /**
         * Returns the affiliation associated with the specified value.
         *
         * @param value the value.
         * @return the associated affiliation.
         */
        public static Affiliation valueOf(int value) {
            switch (value) {
                case 10: return owner;
                case 20: return admin;
                case 30: return member;
                case 40: return outcast;
                default: return none;
            }
        }
    }
	
}
