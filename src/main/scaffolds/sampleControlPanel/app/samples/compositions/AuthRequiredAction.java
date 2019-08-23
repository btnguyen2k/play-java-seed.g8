package samples.compositions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import play.Logger;
import play.mvc.Action;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import samples.bo.user.UserBo;
import samples.utils.SessionUtils;

public class AuthRequiredAction extends Action<AuthRequired> {
    private final Logger.ALogger LOGGER = Logger.of(AuthRequiredAction.class);

    private CompletionStage<Result> goLogin(Http.Request request, String _loginCall, String flashKey, String flashMsg) {
        return CompletableFuture.supplyAsync(() -> {
            String loginCall = _loginCall.trim();
            Http.Flash flash = request.flash();
            if (!StringUtils.isBlank(flashMsg) && !StringUtils.isBlank(flashKey)) {
                flash = flash.adding(flashKey, flashMsg);
            }

            String urlReturn = request.uri();
            if (StringUtils.isBlank(loginCall)) {
                return redirect(samples.controllers.routes.SampleController.login(urlReturn)).withFlash(flash);
            } else if (loginCall.startsWith("/")) {
                return redirect(loginCall).withFlash(flash);
            } else {
                try {
                    String[] tokens = loginCall.split(":");
                    if (tokens.length == 3) {
                        Class<?> clazzContainer = Class.forName(tokens[0]);
                        Field field = clazzContainer.getField(tokens[1]);
                        Class<?> revertClass = field.getType();
                        Method[] methods = revertClass.getMethods();
                        for (Method method : methods) {
                            if (StringUtils.equals(method.getName(), tokens[2])) {
                                return redirect((Call) method.invoke(field.get(null), urlReturn)).withFlash(flash);
                            }
                        }
                    }
                    LOGGER.warn("Cannot find loginCall: " + _loginCall);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            return redirect("/");
        });
    }

    @Override
    public CompletionStage<Result> call(Http.Request request) {
        UserBo currentUser = SessionUtils.currentUser(request);
        if (currentUser == null
                || (configuration.usergroups() != null && configuration.usergroups().length > 0
                && ArrayUtils.indexOf(configuration.usergroups(), currentUser.getGroupId()) == ArrayUtils.INDEX_NOT_FOUND)) {
            if (currentUser != null) {
                LOGGER.warn("User [" + currentUser.getUsername() + "] logged in, but not in allowed groups to access " + request.uri());
            }
            // user not logged in, or not in allowed groups
            return goLogin(request, configuration.loginCall(), configuration.flashKey(), configuration.flashMsg());
        }

        try {
            return delegate.call(request);
        } catch (Exception e) {
            return CompletableFuture.supplyAsync(() -> {
                StringBuilder sb = new StringBuilder(
                        "Error occured, refresh the page to retry. If the error persists, please contact site admin for support.");
                String stacktrace = ExceptionUtils.getStackTrace(e);
                sb.append("\n\nError details: ").append(e.getMessage()).append("\n")
                        .append(stacktrace);
                Throwable cause = e.getCause();
                while (cause != null) {
                    sb.append("\n").append(ExceptionUtils.getStackTrace(cause));
                    cause = cause.getCause();
                }
                return ok(sb.toString());
            });
        }
    }
}
