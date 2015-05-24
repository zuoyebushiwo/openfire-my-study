package org.jivesoftware.openfire.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.RoutableChannelHandler;
import org.jivesoftware.openfire.RoutingTable;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.container.BasicModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/**
 * Manages the registration and delegation of Components. The ComponentManager
 * is responsible for managing registration and delegation of {@link Component Components},
 * as well as offering a facade around basic server functionallity such as sending and
 * receiving of packets.<p>
 *
 * This component manager will be an internal service whose JID will be component.[domain]. So the
 * component manager will be able to send packets to other internal or external components and also
 * receive packets from other components or even from trusted clients (e.g. ad-hoc commands).
 *
 * @author Derek DeMoro
 */
public class InternalComponentManager extends BasicModule implements ComponentManager, RoutableChannelHandler {
	
	private static final Logger Log = LoggerFactory.getLogger(InternalComponentManager.class);
	
	final private Map<String, RoutableComponents> routables = new ConcurrentHashMap<String, RoutableComponents>();
	private Map<String, IQ> componentInfo = new ConcurrentHashMap<String, IQ>();
    private Map<JID, JID> presenceMap = new ConcurrentHashMap<JID, JID>();
    /**
     * Holds the list of listeners that will be notified of component events.
     */
    private List<ComponentEventListener> listeners = new CopyOnWriteArrayList<ComponentEventListener>();
    
    private static InternalComponentManager instance;
    /**
     * XMPP address of this internal service. The address is of the form: component.[domain]
     */
    private JID serviceAddress;
    /**
     * Holds the domain of the server. We are using an iv since we use this value many times
     * in many methods.
     */
    private String serverDomain;
    private RoutingTable routingTable;

	private static class RoutableComponents implements RoutableChannelHandler {
		
		private JID jid;
		final private List<Component> components = new ArrayList<Component>();
		
		public RoutableComponents(JID jid, Component component) {
            this.jid = jid;
            addComponent(component);
        }

		public void addComponent(Component component) {
			synchronized (components) {
                components.add(component);
            }
		}
		
		public void removeComponent(Component component) {
            synchronized (components) {
                components.remove(component);
            }
        }

        public void removeAllComponents() {
            synchronized (components) {
                components.clear();
            }
        }

        public Boolean hasComponent(Component component) {
            return components.contains(component);
        }

        public Integer numberOfComponents() {
            return components.size();
        }

        public List<Component> getComponents() {
            return components;
        }

        private Component getNextComponent() {
            Component component;
            synchronized (components) {
                component = components.get(0);
                Collections.rotate(components, 1);
            }
            return component;
        }

		@Override
		public void process(Packet packet) throws UnauthorizedException,
				PacketException {
			Component component = getNextComponent();
			component.processPacket(packet);
		}

		@Override
		public JID getAddress() {
			return jid;
		}
		
	}
	
}
