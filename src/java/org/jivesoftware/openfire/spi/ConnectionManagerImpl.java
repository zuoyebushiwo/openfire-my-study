package org.jivesoftware.openfire.spi;

import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.jivesoftware.openfire.ConnectionManager;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.util.CertificateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManagerImpl extends BasicModule implements ConnectionManager, CertificateEventListener {
	
	private static final Logger Log = LoggerFactory.getLogger(ConnectionManagerImpl.class);
	
	private SocketAcceptor socketAcceptor;
    private SocketAcceptor sslSocketAcceptor;
    private SocketAcceptor componentAcceptor;

}
