package org.jivesoftware.openfire;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jivesoftware.openfire.cluster.NodeID;
import org.jivesoftware.openfire.container.Module;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main XMPP server that will load, initialize and start all the server's
 * modules. The server is unique in the JVM and could be obtained by using the
 * {@link #getInstance()} method.
 * <p>
 * The loaded modules will be initialized and may access through the server other
 * modules. This means that the only way for a module to locate another module is
 * through the server. The server maintains a list of loaded modules.
 * </p>
 * <p>
 * After starting up all the modules the server will load any available plugin.
 * For more information see: {@link org.jivesoftware.openfire.container.PluginManager}.
 * </p>
 * <p>A configuration file keeps the server configuration. This information is required for the
 * server to work correctly. The server assumes that the configuration file is named
 * <b>openfire.xml</b> and is located in the <b>conf</b> folder. The folder that keeps
 * the configuration file must be located under the home folder. The server will try different
 * methods to locate the home folder.</p>
 * <ol>
 * <li><b>system property</b> - The server will use the value defined in the <i>openfireHome</i>
 * system property.</li>
 * <li><b>working folder</b> -  The server will check if there is a <i>conf</i> folder in the
 * working directory. This is the case when running in standalone mode.</li>
 * <li><b>openfire_init.xml file</b> - Attempt to load the value from openfire_init.xml which
 * must be in the classpath</li>
 * </ol>
 *
 * @author Gaston Dombiak
 */
public class XMPPServer {
	
	private static final Logger Log = LoggerFactory.getLogger(XMPPServer.class);
	
	private static XMPPServer instance;
	
	private String name;
    private String host;
    private Version version;
    private Date startDate;
    private boolean initialized = false;
    private boolean started = false;
    private NodeID nodeID;
    private static final NodeID DEFAULT_NODE_ID = NodeID.getInstance(new byte[0]);
    
    public static final String EXIT = "exit";
    
    /**
     * All modules loaded by this server
     */
    private Map<Class, Module> modules = new LinkedHashMap<Class, Module>();
    
    /**
     * Listeners that will be notified when the server has started or is about to be stopped.
     */
    private List<XMPPServerListener> listeners = new CopyOnWriteArrayList<XMPPServerListener>();
    
    /**
     * Location of the home directory. All configuration files should be
     * located here.
     */
    private File openfireHome;
    private ClassLoader loader;
    
    private PluginManager pluginManager;
    private InternalComponentManager componentManager;

}
