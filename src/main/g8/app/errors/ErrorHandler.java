package errors;

import com.github.ddth.commons.utils.TypesafeConfigUtils;
import com.typesafe.config.Config;
import org.apache.commons.lang3.StringUtils;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Custom errors handler.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r9
 */
@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {
    protected final Config config;
    protected final Environment environment;
    protected final OptionalSourceMapper sourceMapper;
    protected final Provider<Router> routes;

    @Inject
    public ErrorHandler(Config config, Environment environment, OptionalSourceMapper sourceMapper,
            Provider<Router> routes) {
        super(config, environment, sourceMapper, routes);
        this.config = config;
        this.environment = environment;
        this.sourceMapper = sourceMapper;
        this.routes = routes;
    }

    @Override
    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        String viewKey = "error_pages.view_" + statusCode;
        String viewName = TypesafeConfigUtils.getString(config, viewKey);
        if (StringUtils.isBlank(viewName)) {
            return super.onClientError(request, statusCode, message);
        } else {
            try {
                String clazzName = "views.html." + viewName;
                Class<?> clazz = Class.forName(clazzName);
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("render")) {
                        Object[] params = { request, statusCode, message };
                        return CompletableFuture.completedFuture(Results.ok((Html) method.invoke(null, params)));
                    }
                }
            } catch (ClassNotFoundException e) {
                return super.onClientError(request, statusCode, message);
            } catch (Exception e) {
                return onServerError(request, e);
            }
        }
        return super.onClientError(request, statusCode, message);
    }

    @Override
    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {
        if (exception instanceof ClientError) {
            ClientError clientError = (ClientError) exception;
            return onClientError(request, clientError.statusCode, clientError.message);
        }
        return super.onServerError(request, exception);
    }

    private CompletionStage<Result> serverError(boolean devMode, RequestHeader request, UsefulException exception) {
        String viewKey = "error_pages.view_server";
        String viewName = TypesafeConfigUtils.getString(config, viewKey);
        if (StringUtils.isBlank(viewName)) {
            return devMode ? super.onDevServerError(request, exception) : super.onProdServerError(request, exception);
        } else {
            try {
                String clazzName = "views.html." + viewName;
                Class<?> clazz = Class.forName(clazzName);
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("render")) {
                        Object[] params = { request, exception.cause };
                        return CompletableFuture.completedFuture(Results.ok((Html) method.invoke(null, params)));
                    }
                }
            } catch (ClassNotFoundException e) {
                return devMode ?
                        super.onDevServerError(request, exception) :
                        super.onProdServerError(request, exception);
            } catch (Exception e) {
                return devMode ?
                        super.onDevServerError(request, throwableToUsefulException(e)) :
                        super.onProdServerError(request, throwableToUsefulException(e));
            }
        }
        return devMode ? super.onDevServerError(request, exception) : super.onProdServerError(request, exception);
    }

    @Override
    protected CompletionStage<Result> onDevServerError(RequestHeader request, UsefulException exception) {
        boolean overrideDevMode = TypesafeConfigUtils.getBooleanOptional(config, "error_pages.override_dev_mode")
                .orElse(Boolean.FALSE).booleanValue();
        if (!overrideDevMode) {
            return super.onDevServerError(request, exception);
        }
        return serverError(true, request, exception);
    }

    @Override
    protected CompletionStage<Result> onProdServerError(RequestHeader request, UsefulException exception) {
        return serverError(false, request, exception);
    }
}
