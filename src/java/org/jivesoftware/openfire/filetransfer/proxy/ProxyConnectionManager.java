package org.jivesoftware.openfire.filetransfer.proxy;

import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jivesoftware.openfire.filetransfer.FileTransferManager;
import org.jivesoftware.openfire.stats.Statistic;
import org.jivesoftware.openfire.stats.StatisticsManager;
import org.jivesoftware.openfire.stats.i18nStatistic;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the connections to the proxy server. The connections go through two stages before
 * file transfer begins. The first stage is when the file transfer target initiates a connection
 * to this manager. Stage two is when the initiator connects, the manager will then match the two
 * connections using the unique SHA-1 hash defined in the SOCKS5 protocol.
 *
 * @author Alexander Wenckus
 */
public class ProxyConnectionManager {
	
	private static final Logger Log = LoggerFactory.getLogger(ProxyConnectionManager.class);

    private static final String proxyTransferRate = "proxyTransferRate";
    
    private Map<String, ProxyTransfer> connectionMap;
    
    private final Object connectionLock = new Object();
    
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    private Future<?> socketProcess;
    
    private ServerSocket serverSocket;
    
    private int proxyPort;
    
    private FileTransferManager transferManager;
    
    private String className;
    
    public ProxyConnectionManager(FileTransferManager manager) {
        String cacheName = "File Transfer";
        connectionMap = CacheFactory.createCache(cacheName);

        className = JiveGlobals.getProperty("provider.transfer.proxy",
                "org.jivesoftware.openfire.filetransfer.proxy.DefaultProxyTransfer");

        transferManager = manager;
        StatisticsManager.getInstance().addStatistic(proxyTransferRate, new ProxyTracker());
    }
    
    private static class ProxyTracker extends i18nStatistic {
        public ProxyTracker() {
            super("filetransferproxy.transfered", Statistic.Type.rate);
        }

        public double sample() {
            return (ProxyOutputStream.amountTransfered.getAndSet(0) / 1000d);
        }

        public boolean isPartialSample() {
            return true;
        }
    }

}
