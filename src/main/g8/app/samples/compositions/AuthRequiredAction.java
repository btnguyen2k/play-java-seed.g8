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
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import samples.bo.user.UserBo;
import samples.utils.SessionUtils;

public class AuthRequiredAction extends Action<AuthRequired> {

    private CompletionStage<Result> goLogin(Context ctx, String _loginCall, String flashKey,
            String flashMsg) {
        return CompletableFuture.supplyAsync(() -> {
            String loginCall = _loginCall.trim();
            if (!StringUtils.isBlank(flashMsg) && !StringUtils.isBlank(flashKey)) {
                ctx.flash().put(flashKey, flashMsg);
            }

            String urlReturn = ctx.request().uri();
            if (StringUtils.isBlank(loginCall)) {
                return redirect(samples.controllers.routes.SampleController.login(urlReturn));
            } else if (loginCall.startsWith("/")) {
                return redirect(loginCall);
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
                                return redirect((Call) method.invoke(field.get(null), urlReturn));
                            }
                        }
                    }
                    Logger.warn("Cannot find loginCall: " + _loginCall);
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
            }
            return redirect("/");
        });
    }

    @Override
    public CompletionStage<Result> call(Context ctx) {
        Session session = ctx.session();
        UserBo currentUser = SessionUtils.currentUser(session);
        if (currentUser == null
                || (configuration.usergroups() != null && configuration.usergroups().length > 0
                && ArrayUtils.indexOf(configuration.usergroups(),
                currentUser.getGroupId()) == ArrayUtils.INDEX_NOT_FOUND)) {
            // user not logged in, or not in allowed groups
            return goLogin(ctx, configuration.loginCall(), configuration.flashKey(),
                    configuration.flashMsg());
        }

        Logger.debug("" + session);

        try {
            return delegate.call(ctx);
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
