package modules.grpc;

import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import io.grpc.Server;
import modules.registry.IRegistry;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.AppConfigUtils;

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

    private Provider<IRegistry> registry;
    private Application playApp;
    private Config appConfig;

    private Server grpcApiGateway, grpcApiGatewaySsl;

    /**
     * {@inheritDoc}
     */
    @Inject
    public GrpcServiceBootstrap(ApplicationLifecycle lifecycle, Application playApp,
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
        int grpcPort = 9095;
        try {
            grpcPort = Integer
                    .parseInt(System.getProperty("grpc.port", playApp.isDev() ? "9095" : "0"));
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            grpcPort = 0;
        }
        int grpcPortSsl = 9098;
        try {
            grpcPortSsl = Integer
                    .parseInt(System.getProperty("grpc.ssl_port", playApp.isDev() ? "9098" : "0"));
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            grpcPortSsl = 0;
        }

        if (grpcPort > 0 || grpcPortSsl > 0) {
            /* at least one of gRPC or gRPC-SSL is enabled */
            String grpcHost = System.getProperty("grpc.addr", "0.0.0.0");

            // prepare configurations.
            int maxHeaderListSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.grpc.maxHeaderListSize", 0);
            if (maxHeaderListSize <= 0) {
                maxHeaderListSize = 8 * 1024;
            }
            int maxMsgSize = AppConfigUtils
                    .getOrDefault(appConfig::getInt, "api.grpc.maxMessageSize", 0);
            if (maxMsgSize <= 0) {
                maxMsgSize = 64 * 1024;
            }

            ApiRouter apiRouter = registry.get().getApiRouter();
            if (grpcPort > 0) {
                try {
                    Logger.info(
                            "Starting gRPC API Gateway on [" + grpcHost + ":" + grpcPort + "]...");
                    Server grpcServer = GrpcApiUtils
                            .createGrpcServer(apiRouter, grpcHost, grpcPort, maxHeaderListSize,
                                    maxMsgSize);
                    grpcServer.start();
                } catch (Exception e) {
                    Logger.error(e.getMessage(), e);
                }
            }

            if (grpcPortSsl > 0) {
                try {
                    String certChainFilePath = System.getProperty("grpc.ssl.certChain");
                    File certChainFile =
                            certChainFilePath != null ? new File(certChainFilePath) : null;
                    String privateKeyFilePath = System.getProperty("grpc.ssl.privKey");
                    File privateKeyFile =
                            privateKeyFilePath != null ? new File(privateKeyFilePath) : null;
                    String keyFilePassword = System.getProperty("grpc.ssl.privKeyPassword");
                    if (certChainFile == null) {
                        Logger.warn("Certificate chain file is not specified!");
                    } else if (!certChainFile.isFile() || !certChainFile.canRead()) {
                        Logger.warn(
                                "Certificate chain file not found or not readable [" + certChainFile
                                        .getAbsolutePath() + "]!");
                    } else if (privateKeyFile == null) {
                        Logger.warn("Private key file is not specified!");
                    } else if (!privateKeyFile.isFile() || !privateKeyFile.canRead()) {
                        Logger.warn("Private key not found or not readable [" + privateKeyFile
                                .getAbsolutePath() + "]!");
                    } else {
                        Logger.info(
                                "Starting gRPC API Gateway SSL on [" + grpcHost + ":" + grpcPortSsl
                                        + "]...");
                        Server grpcServerSsl = GrpcApiUtils
                                .createGrpcServerSsl(apiRouter, grpcHost, grpcPortSsl,
                                        maxHeaderListSize, maxMsgSize, certChainFile,
                                        privateKeyFile, keyFilePassword);
                        grpcServerSsl.start();
                    }
                } catch (Exception e) {
                    Logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void destroy() {
        if (grpcApiGateway != null) {
            try {
                Logger.info("Stopping gRPC API Gateway...");
                grpcApiGateway.shutdown();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            } finally {
                grpcApiGateway = null;
            }
        }

        if (grpcApiGatewaySsl != null) {
            try {
                Logger.info("Stopping gRPC API Gateway SSL...");
                grpcApiGatewaySsl.shutdown();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            } finally {
                grpcApiGatewaySsl = null;
            }
        }
    }

}
