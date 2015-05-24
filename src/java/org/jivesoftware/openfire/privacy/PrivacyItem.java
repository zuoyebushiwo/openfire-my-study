package org.jivesoftware.openfire.privacy;

import org.dom4j.Element;
import org.jivesoftware.util.cache.Cacheable;
import org.xmpp.packet.JID;

/**
 * A privacy item acts a rule that when matched defines if a packet should be blocked or not. 
 *
 * @author Gaston Dombiak
 */
class PrivacyItem implements Cacheable, Comparable {

	private int order;
    private boolean allow;
    private Type type;
    private JID jidValue;
    private RosterItem.SubType subscriptionValue;
    private String groupValue;
    private boolean filterEverything;
    private boolean filterIQ;
    private boolean filterMessage;
    private boolean filterPresence_in;
    private boolean filterPresence_out;
    /**
     * Copy of the element that defined this item.
     */
    private Element itemElement;
    
    /**
     * Type defines if the rule is based on JIDs, roster groups or presence subscription types.
     */
    private static enum Type {
        /**
         * JID being analyzed should belong to a roster group of the list's owner.
         */
        group,
        /**
         * JID being analyzed should have a resource match, domain match or bare JID match.
         */
        jid,
        /**
         * JID being analyzed should belong to a contact present in the owner's roster with
         * the specified subscription status.
         */
        subscription
    }
}
