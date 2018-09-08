package modules.thriftservice;

import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import modules.registry.IRegistry;
import org.apache.thrift.server.TServer;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.AppConfigUtils;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Thrift API Gateway Bootstraper.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ThriftServiceBootstrap {

    private Provider<IRegistry> registry;
    private Application playApp;
    private Config appConfig;

    private TServer thriftApiGateway, thriftApiGatewaySsl;

    /**
     * {@inheritDoc}
     */
    @Inject
    public ThriftServiceBootstrap(ApplicationLifecycle lifecycle, Application playApp,
            Provider<IRegistry> registry) {
        this.playApp = playApp;
        this.appConfig = playApp.config();
        this.registry = registry;

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
        try {
            thriftPort = Integer
                    .parseInt(System.getProperty("thrift.port", playApp.isDev() ? "9090" : "0"));
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            thriftPort = 0;
        }
        int thriftPortSsl = 9093;
        try {
            thriftPortSsl = Integer.parseInt(
                    System.getProperty("thrift.ssl_port", playApp.isDev() ? "9093" : "0"));
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            thriftPortSsl = 0;
        }

        if (thriftPort > 0 || thriftPortSsl > 0) {
            /* at least one of Thrift or ThriftSSL is enabled */
            String thriftHost = System.getProperty("thrift.addr", "0.0.0.0");

            // prepare configurations.
            int clientTimeoutMillisecs = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.thrift.clientTimeout", 0);
            int maxFrameSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.thrift.maxFrameSize", 0);
            int maxReadBufferSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.thrift.maxReadBufferSize", 0);
            int numSelectorThreads = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.thrift.selectorThreads", 0);
            int numWorkerThreads = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.thrift.workerThreads", 0);
            int queueSizePerThread = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.thrift.queueSizePerThread", 0);

            ApiRouter apiRouter = registry.get().getApiRouter();
            if (thriftPort > 0) {
                try {
                    Logger.info("Starting Thrift API Gateway on [" + thriftHost + ":" + thriftPort
                            + "/compactMode=true]...");
                    thriftApiGateway = ThriftApiUtils
                            .createThriftServer(apiRouter, true, thriftHost, thriftPort,
                                    clientTimeoutMillisecs, maxFrameSize, maxReadBufferSize,
                                    numSelectorThreads, numWorkerThreads, queueSizePerThread);
                    Thread thriftThread = ThriftApiUtils
                            .startThriftServer(thriftApiGateway, "Thrift API Gateway", true);
                } catch (Exception e) {
                    Logger.error(e.getMessage(), e);
                }
            }

            if (thriftPortSsl > 0) {
                try {
                    Logger.info("Starting Thrift API Gateway SSL on [" + thriftHost + ":"
                            + thriftPortSsl + "/compactMode=true]...");
                    String keystorePath = System.getProperty("thrift.ssl.keyStore");
                    File keystore = keystorePath != null ? new File(keystorePath) : null;
                    String keystorePassword = System.getProperty("thrift.ssl.keyStorePassword");
                    if (keystore == null) {
                        Logger.warn("Keystore file is not specified!");
                    } else if (!keystore.isFile() || !keystore.canRead()) {
                        Logger.warn("Keystore file not found or not readable [" + keystore
                                .getAbsolutePath() + "]!");
                    } else {
                        thriftApiGatewaySsl = ThriftApiUtils
                                .createThriftServerSsl(apiRouter, true, thriftHost, thriftPortSsl,
                                        clientTimeoutMillisecs, numWorkerThreads, keystore,
                                        keystorePassword);
                        Thread thriftThreadSsl = ThriftApiUtils
                                .startThriftServer(thriftApiGatewaySsl, "Thrift API Gateway SSL",
                                        true);
                    }
                } catch (Exception e) {
                    Logger.error(e.getMessage(), e);
                }
            }
        }
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

}
