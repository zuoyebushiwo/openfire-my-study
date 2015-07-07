package org.jivesoftware.openfire.muc.spi;

import org.jivesoftware.openfire.cluster.NodeID;
import org.jivesoftware.openfire.muc.MUCRole;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.NotAllowedException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * Implementation of a local room occupant.
 * 
 * @author Gaston Dombiak
 */
public class LocalMUCRole implements MUCRole {

	/**
     * The room this role is valid in.
     */
	
	@Override
	public Presence getPresence() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPresence(Presence presence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRole(Role newRole) throws NotAllowedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Role getRole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAffiliation(Affiliation newAffiliation)
			throws NotAllowedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Affiliation getAffiliation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeNickname(String nickname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNickname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isVoiceOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MUCRoom getChatRoom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JID getRoleAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JID getUserAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NodeID getNodeID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Packet packet) {
		// TODO Auto-generated method stub
		
	}

}
