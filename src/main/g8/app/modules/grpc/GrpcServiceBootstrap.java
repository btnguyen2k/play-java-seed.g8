package modules.grpc;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.google.inject.Provider;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import modules.registry.IRegistry;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;

/**
 * gRPC API Gateway Bootstraper.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r2
 */
public class GrpcServiceBootstrap {

    private Provider<IRegistry> registry;
    private Application         playApp;
    private Server              server;

    /**
     * {@inheritDoc}
     */
    @Inject
    public GrpcServiceBootstrap(ApplicationLifecycle lifecycle, Application playApp,
            Provider<IRegistry> registry) {
        this.playApp = playApp;
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
            grpcPort = Integer.parseInt(System.getProperty("grpc.port",
                    playApp.isDev() ? String.valueOf(grpcPort) : "0"));
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            grpcPort = 0;
        }

        if (grpcPort > 0) {
            if (grpcPort > 65535) {
                Logger.warn("Invalid port value for gRPC API Gateway [" + grpcPort + "]!");
            } else {
                Logger.info("Starting gRpc API Gateway on port " + grpcPort + "...");
                server = ServerBuilder.forPort(grpcPort)
                        .addService(new grpc.ApiServiceHandler(registry)).build().start();
                Logger.info("gRpc API Gateway started on port " + grpcPort);
            }
        }
    }

    private void destroy() {
        if (server != null) {
            try {
                Logger.info("Stopping gRPC API Gateway...");
                server.shutdown();
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
            } finally {
                server = null;
            }
        }
    }

}
