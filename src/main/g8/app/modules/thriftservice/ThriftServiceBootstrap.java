package modules.thriftservice;

import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;
import com.github.ddth.recipes.global.GlobalRegistry;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import modules.registry.IRegistry;
import org.apache.thrift.server.TServer;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.AppConfigUtils;
import utils.AppConstants;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Thrift API Gateway Bootstrapper.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ThriftServiceBootstrap {
    private final Logger.ALogger LOGGER = Logger.of(ThriftServiceBootstrap.class);

    private Provider<IRegistry> registry;
    private Application playApp;
    private Config appConfig;

    /**
     * {@inheritDoc}
     */
    @Inject
    public ThriftServiceBootstrap(ApplicationLifecycle lifecycle, Application playApp, Provider<IRegistry> registry) {
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
    private void init() {
        int thriftPort = 9090;
        try {
            thriftPort = Integer.parseInt(System.getProperty("thrift.port", playApp.isDev() ? "9090" : "0"));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            thriftPort = 0;
        }
        int thriftPortSsl = 9093;
        try {
            thriftPortSsl = Integer.parseInt(System.getProperty("thrift.ssl_port", playApp.isDev() ? "9093" : "0"));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            thriftPortSsl = 0;
        }

        if (thriftPort > 0 || thriftPortSsl > 0) {
            /* at least one of Thrift or Thrift-SSL is enabled */
            String thriftHost = System.getProperty("thrift.addr", "0.0.0.0");

            // prepare configurations.
            int clientTimeoutMillisecs = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_THRIFT_CLIENT_TIMEOUT,
                            AppConstants.DEFAULT_API_CONF_THRIFT_CLIENT_TIMEOUT);
            int maxFrameSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_THRIFT_MAX_FRAME_SIZE,
                            AppConstants.DEFAULT_API_CONF_THRIFT_MAX_FRAME_SIZE);
            int maxReadBufferSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_THRIFT_MAX_READ_BUFFER_SIZE,
                            AppConstants.DEFAULT_API_CONF_THRIFT_MAX_READ_BUFFER_SIZE);
            int numSelectorThreads = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_THRIFT_NUM_SELECTOR_THREADS,
                            AppConstants.DEFAULT_API_CONF_THRIFT_NUM_SELECTOR_THREADS);
            int numWorkerThreads = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_THRIFT_NUM_WORKER_THREADS,
                            AppConstants.DEFAULT_API_CONF_THRIFT_NUM_WORKER_THREADS);
            int queueSizePerThread = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_THRIFT_QUEUE_SIZE_PER_THREAD,
                            AppConstants.DEFAULT_API_CONF_THRIFT_QUEUE_SIZE_PER_THREAD);
            boolean compactNode = AppConfigUtils
                    .getOrDefault(appConfig::getBoolean, AppConstants.API_CONF_THRIFT_COMPACT_MODE,
                            AppConstants.DEFAULT_API_CONF_THRIFT_COMPACT_MODE);

            ApiRouter apiRouter = registry.get().getApiRouter();
            if (thriftPort > 0) {
                try {
                    LOGGER.info("Starting Thrift API Gateway on [" + thriftHost + ":" + thriftPort + "/compactMode="
                            + compactNode + "]...");
                    TServer thriftApiGateway = ThriftUtils
                            .createThriftServer(apiRouter, compactNode, thriftHost, thriftPort, clientTimeoutMillisecs,
                                    maxFrameSize, maxReadBufferSize, numSelectorThreads, numWorkerThreads,
                                    queueSizePerThread);
                    ThriftUtils.startThriftServer(thriftApiGateway, "Thrift API Gateway", true);
                    GlobalRegistry.addShutdownHook(() -> {
                        try {
                            LOGGER.info("Stopping Thrift API Gateway...");
                            thriftApiGateway.stop();
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    });
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            if (thriftPortSsl > 0) {
                try {
                    String keystorePath = System.getProperty("thrift.ssl.keyStore");
                    File keystore = keystorePath != null ? new File(keystorePath) : null;
                    String keystorePassword = System.getProperty("thrift.ssl.keyStorePassword");
                    if (keystore == null) {
                        LOGGER.error(
                                "Keystore file is not specified via system property [thrift.ssl.keyStorePassword]. Thrift API Gateway SSL will be disabled.");
                    } else if (!keystore.isFile() || !keystore.canRead()) {
                        LOGGER.error("Keystore file not found or not readable [" + keystore.getAbsolutePath()
                                + "]. Thrift API Gateway SSL will be disabled.");
                    } else {
                        LOGGER.info(
                                "Starting Thrift API Gateway SSL on [" + thriftHost + ":" + thriftPortSsl + compactNode
                                        + "]...");
                        TServer thriftApiGatewaySsl = ThriftUtils
                                .createThriftServerSsl(apiRouter, compactNode, thriftHost, thriftPortSsl,
                                        clientTimeoutMillisecs, numWorkerThreads, keystore, keystorePassword);
                        ThriftUtils.startThriftServer(thriftApiGatewaySsl, "Thrift API Gateway SSL", true);
                        GlobalRegistry.addShutdownHook(() -> {
                            try {
                                LOGGER.info("Stopping Thrift API Gateway SSL...");
                                thriftApiGatewaySsl.stop();
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        });
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private void destroy() {
        //EMPTY
    }
}
