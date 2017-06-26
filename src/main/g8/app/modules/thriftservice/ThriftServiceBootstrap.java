package modules.thriftservice;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.server.TThreadedSelectorServer.Args.AcceptPolicy;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import com.google.inject.Provider;

import modules.registry.IRegistry;
import play.Application;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;
import thrift.ApiServiceHandler;
import thrift.def.TApiService;

/**
 * Thrift API Gateway Bootstraper.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ThriftServiceBootstrap {

    private Application playApp;
    private Configuration appConfig;
    private Provider<IRegistry> registry;
    private TServer thriftApiGateway, thriftApiGatewaySsl;

    /**
     * {@inheritDoc}
     */
    @Inject
    public ThriftServiceBootstrap(ApplicationLifecycle lifecycle, Application playApp,
            Provider<IRegistry> registry) {
        this.playApp = playApp;
        this.appConfig = playApp.configuration();
        this.registry = registry;

        // for Java 8+
        lifecycle.addStopHook(() -> {
            destroy();
            return CompletableFuture.completedFuture(null);
        });

        try {
            init();
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    /*----------------------------------------------------------------------*/
    private void init() throws Exception {
        int thriftPort = 9090;
        if (!playApp.isDev()) {
            try {
                thriftPort = Integer.parseInt(System.getProperty("thrift.port", "0"));
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
                thriftPort = 0;
            }
        }
        int thriftPortSsl = 9093;
        if (!playApp.isDev()) {
            try {
                thriftPortSsl = Integer.parseInt(System.getProperty("thrift.ssl_port", "0"));
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
                thriftPortSsl = 0;
            }
        }

        if (thriftPort > 0 || thriftPortSsl > 0) {
            /* at least one of thrift of thriftSsl is enabled */

            // prepare configurations.
            int clientTimeoutMillisecs = appConfig.getInt("api.thrift.clientTimeout", 0);
            int maxFrameSize = appConfig.getInt("api.thrift.maxFrameSize", 0);
            int maxReadBufferSize = appConfig.getInt("api.thrift.maxReadBufferSize", 0);
            int numSelectorThreads = appConfig.getInt("api.thrift.selectorThreads", 0);
            int numWorkerThreads = appConfig.getInt("api.thrift.workerThreads", 0);
            int queueSizePerThread = appConfig.getInt("api.thrift.queueSizePerThread", 0);

            TProcessorFactory processorFactory = new TProcessorFactory(
                    new TApiService.Processor<TApiService.Iface>(new ApiServiceHandler(registry)));
            TProtocolFactory protocolFactory = new TCompactProtocol.Factory();

            if (thriftPort <= 0) {
                Logger.info("Thrift API Gateway is disabled!");
            } else {
                if (thriftPort > 65535) {
                    Logger.warn("Invalid port value for Thrift API Gateway [" + thriftPort + "]!");
                } else {
                    Logger.info("Starting Thrift API Gateway on port " + thriftPort + "...");
                    thriftApiGateway = createThreadedSelectorServer(processorFactory,
                            protocolFactory, thriftPort, numSelectorThreads, numWorkerThreads,
                            queueSizePerThread, clientTimeoutMillisecs, maxFrameSize,
                            maxReadBufferSize);
                    runThriftServer(thriftApiGateway, false);
                    Logger.info("Thrift API Gateway started on port " + thriftPort);
                }
            }

            if (thriftPortSsl <= 0) {
                Logger.info("Thrift API Gateway SSL is disabled!");
            } else {
                String keystorePath = System.getProperty("javax.net.ssl.keyStore");
                File keystore = keystorePath != null ? new File(keystorePath) : null;
                String keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
                if (thriftPortSsl > 65535) {
                    Logger.warn("Invalid port value for Thrift API Gateway SSL [" + thriftPortSsl
                            + "]!");
                } else if (keystore == null) {
                    Logger.warn("Keystore file is not specified!");
                } else if (!keystore.isFile() || !keystore.canRead()) {
                    Logger.warn("Keystore file not found or not readable ["
                            + keystore.getAbsolutePath() + "]!");
                } else {
                    Logger.info("Starting Thrift API Gateway SSL on port " + thriftPortSsl + "...");
                    thriftApiGatewaySsl = createThreadedPoolServerSsl(processorFactory,
                            protocolFactory, thriftPortSsl, numWorkerThreads,
                            clientTimeoutMillisecs, maxFrameSize, maxReadBufferSize, keystore,
                            keystorePassword);
                    runThriftServer(thriftApiGatewaySsl, true);
                    Logger.info("Thrift API Gateway SSL started on port " + thriftPortSsl);
                }
            }
        }
    }

    private TServer createThreadedPoolServerSsl(TProcessorFactory processorFactory,
            TProtocolFactory protocolFactory, int port, int numWorkerThreads,
            int clientTimeoutMillisecs, int maxFrameSize, long maxReadBufferSize, File keystore,
            String keystorePass) throws TTransportException, UnknownHostException {
        if (numWorkerThreads < 1) {
            numWorkerThreads = 8;
        }
        InetAddress listenAddr = InetAddress
                .getByName(System.getProperty("thrift.addr", "0.0.0.0"));

        TSSLTransportParameters sslParams = new TSSLTransportParameters();
        sslParams.setKeyStore(keystore.getAbsolutePath(), keystorePass);
        TServerTransport transport = TSSLTransportFactory.getServerSocket(port,
                clientTimeoutMillisecs, listenAddr, sslParams);

        TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .minWorkerThreads(1).maxWorkerThreads(numWorkerThreads);
        TThreadPoolServer server = new TThreadPoolServer(args);
        return server;
    }

    private static TServer createThreadedSelectorServer(TProcessorFactory processorFactory,
            TProtocolFactory protocolFactory, int port, int numSelectorThreads,
            int numWorkerThreads, int queueSizePerThread, int clientTimeoutMillisecs,
            int maxFrameSize, long maxReadBufferSize)
            throws TTransportException, UnknownHostException {
        if (numSelectorThreads < 1) {
            numSelectorThreads = 2;
        }
        if (numWorkerThreads < 1) {
            numWorkerThreads = 8;
        }
        if (queueSizePerThread < 1) {
            queueSizePerThread = 100;
        }
        InetSocketAddress listenAddr = new InetSocketAddress(
                System.getProperty("thrift.addr", "0.0.0.0"), port);

        TNonblockingServerTransport transport = new TNonblockingServerSocket(listenAddr,
                clientTimeoutMillisecs);
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).workerThreads(numWorkerThreads)
                .acceptPolicy(AcceptPolicy.FAIR_ACCEPT).acceptQueueSizePerThread(queueSizePerThread)
                .selectorThreads(numSelectorThreads);
        args.maxReadBufferBytes = maxReadBufferSize;
        TThreadedSelectorServer server = new TThreadedSelectorServer(args);
        return server;
    }

    private void destroy() {
        if (thriftApiGateway != null) {
            try {
                Logger.info("Stopping Thrift API Gateway...");
                thriftApiGateway.stop();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            } finally {
                thriftApiGateway = null;
            }
        }

        if (thriftApiGatewaySsl != null) {
            try {
                Logger.info("Stopping Thrift API Gateway SSL...");
                thriftApiGatewaySsl.stop();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            } finally {
                thriftApiGatewaySsl = null;
            }
        }
    }

    private void runThriftServer(TServer thriftServer, boolean isSsl) {
        String name = isSsl ? "Thrift API Gateway SSL" : "Thrift API Gateway";
        Thread t = new Thread(name) {
            public void run() {
                boolean restart = true;
                while (restart) {
                    try {
                        restart = false;
                        thriftServer.serve();
                        Logger.info(name + " stopped.");
                    } catch (Exception e) {
                        Logger.error(e.getMessage(), e);
                        restart = true;
                    }
                }
            }
        };
        t.start();
    }

}
