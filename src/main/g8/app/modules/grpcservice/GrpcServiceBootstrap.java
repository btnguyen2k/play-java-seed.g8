package modules.grpcservice;

import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;
import com.github.ddth.recipes.global.GlobalRegistry;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import io.grpc.Server;
import modules.registry.IRegistry;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.AppConfigUtils;
import utils.AppConstants;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * gRPC API Gateway Bootstraper.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r2
 */
public class GrpcServiceBootstrap {
    private final Logger.ALogger LOGGER = Logger.of(GrpcServiceBootstrap.class);

    private Provider<IRegistry> registry;
    private Application playApp;
    private Config appConfig;

    /**
     * {@inheritDoc}
     */
    @Inject
    public GrpcServiceBootstrap(ApplicationLifecycle lifecycle, Application playApp, Provider<IRegistry> registry) {
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
        int grpcPort = 9095;
        try {
            grpcPort = Integer.parseInt(System.getProperty("grpc.port", playApp.isDev() ? "9095" : "0"));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            grpcPort = 0;
        }
        int grpcPortSsl = 9098;
        try {
            grpcPortSsl = Integer.parseInt(System.getProperty("grpc.ssl_port", playApp.isDev() ? "9098" : "0"));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            grpcPortSsl = 0;
        }

        if (grpcPort > 0 || grpcPortSsl > 0) {
            /* at least one of gRPC or gRPC-SSL is enabled */
            String grpcHost = System.getProperty("grpc.addr", "0.0.0.0");

            // prepare configurations.
            int maxInboundMetadataSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, AppConstants.API_CONF_GRPC_MAX_INBOUND_METADATA_SIZE, 0);
            if (maxInboundMetadataSize <= 0) {
                maxInboundMetadataSize = AppConstants.DEFAULT_API_CONF_GRPC_MAX_INBOUND_METADATA_SIZE;
            }
            int maxMsgSize = AppConfigUtils.getOrDefault(appConfig::getInt, AppConstants.API_CONF_GRPC_MAX_MSG_SIZE, 0);
            if (maxMsgSize <= 0) {
                maxMsgSize = AppConstants.DEFAULT_API_CONF_GRPC_MAX_MSG_SIZE;
            }

            ApiRouter apiRouter = registry.get().getApiRouter();
            if (grpcPort > 0) {
                try {
                    LOGGER.info("Starting gRPC API Gateway on [" + grpcHost + ":" + grpcPort + "]...");
                    Server grpcServer = GrpcUtils
                            .createGrpcServer(apiRouter, grpcHost, grpcPort, maxInboundMetadataSize, maxMsgSize);
                    grpcServer.start();
                    GlobalRegistry.addShutdownHook(() -> {
                        try {
                            LOGGER.info("Stopping gRPC API Gateway...");
                            grpcServer.shutdown();
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    });
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            if (grpcPortSsl > 0) {
                try {
                    String certChainFilePath = System.getProperty("grpc.ssl.certChain");
                    File certChainFile = certChainFilePath != null ? new File(certChainFilePath) : null;
                    String privateKeyFilePath = System.getProperty("grpc.ssl.privKey");
                    File privateKeyFile = privateKeyFilePath != null ? new File(privateKeyFilePath) : null;
                    String keyFilePassword = System.getProperty("grpc.ssl.privKeyPassword");
                    if (certChainFile == null) {
                        LOGGER.error(
                                "Certificate chain file is not specified via system property [grpc.ssl.certChain]. gRPC API Gateway SSL will be disabled.");
                    } else if (!certChainFile.isFile() || !certChainFile.canRead()) {
                        LOGGER.error(
                                "Certificate chain file not found or not readable [" + certChainFile.getAbsolutePath()
                                        + "]. gRPC API Gateway SSL will be disabled.");
                    } else if (privateKeyFile == null) {
                        LOGGER.error(
                                "Private key file is not specified via system property [grpc.ssl.privKey]. gRPC API Gateway SSL will be disabled.");
                    } else if (!privateKeyFile.isFile() || !privateKeyFile.canRead()) {
                        LOGGER.error("Private key not found or not readable [" + privateKeyFile.getAbsolutePath()
                                + "]. gRPC API Gateway SSL will be disabled.");
                    } else {
                        LOGGER.info("Starting gRPC API Gateway SSL on [" + grpcHost + ":" + grpcPortSsl + "]...");
                        Server grpcServerSsl = GrpcUtils
                                .createGrpcServerSsl(apiRouter, grpcHost, grpcPortSsl, maxInboundMetadataSize,
                                        maxMsgSize, certChainFile, privateKeyFile, keyFilePassword);
                        grpcServerSsl.start();
                        GlobalRegistry.addShutdownHook(() -> {
                            try {
                                LOGGER.info("Stopping gRPC API Gateway SSL...");
                                grpcServerSsl.shutdown();
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
