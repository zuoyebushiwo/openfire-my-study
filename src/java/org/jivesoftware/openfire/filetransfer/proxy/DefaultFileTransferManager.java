package org.jivesoftware.openfire.filetransfer.proxy;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.openfire.filetransfer.FileTransfer;
import org.jivesoftware.openfire.filetransfer.FileTransferInterceptor;
import org.jivesoftware.openfire.filetransfer.FileTransferManager;
import org.jivesoftware.openfire.filetransfer.DefaultFileTransferManager.MetaFileTransferInterceptor;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;
import org.xmpp.packet.Packet;

/**
 * Provides several utility methods for file transfer manager implementaions to utilize.
 *
 * @author Alexander Wenckus
 */
public class DefaultFileTransferManager extends BasicModule implements FileTransferManager {

	private static final String CACHE_NAME = "File Transfer Cache";

    private final Cache<String, FileTransfer> fileTransferMap;

    private final List<FileTransferInterceptor> fileTransferInterceptorList
            = new ArrayList<FileTransferInterceptor>();
    
    /**
     * Default constructor creates the cache.
     */
    public DefaultFileTransferManager() {
        super("File Transfer Manager");
        fileTransferMap = CacheFactory.createCache(CACHE_NAME);
        InterceptorManager.getInstance().addInterceptor(new MetaFileTransferInterceptor());
    }
	
    /**
     * Interceptor to grab and validate file transfer meta information.
     */
    private class MetaFileTransferInterceptor implements PacketInterceptor {

		@Override
		public void interceptPacket(Packet packet, Session session,
				boolean incoming, boolean processed)
				throws PacketRejectedException {
			// We only want packets recieved by the server
			
		}
    	
    }
	
}
