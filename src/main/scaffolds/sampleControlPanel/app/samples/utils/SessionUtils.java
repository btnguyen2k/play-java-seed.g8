package samples.utils;

import modules.registry.IRegistry;
import play.mvc.Http;
import play.mvc.Http.Session;
import play.mvc.Result;
import samples.bo.user.IUserDao;
import samples.bo.user.UserBo;

/**
 * Session-related helper class.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class SessionUtils {
    public final static String SESSION_USERNAME = "u";

    /**
     * Get the currently logged-in user.
     *
     * @param request
     * @return
     */
    public static UserBo currentUser(Http.Request request) {
        return currentUser(request.session());
    }

    /**
     * Get the currently logged-in user.
     *
     * @param session
     * @return
     */
    public static UserBo currentUser(Session session) {
        String username = session.getOptional(SESSION_USERNAME).orElse(null);
        IUserDao dao = IRegistry.INSTANCE.get().getBean(IUserDao.class);
        return dao.getUser(username);
    }

    /**
     * Log a user in.
     *
     * @param result
     * @param user
     * @return
     */
    public static Result login(Result result, UserBo user) {
        return result.withSession(login(result.session(), user));
    }

    /**
     * Log a user in.
     *
     * @param session
     * @param user
     * @return
     */
    public static Session login(Session session, UserBo user) {
        return session.adding(SESSION_USERNAME, user.getUsername());
    }

    /**
     * Log the current user out.
     *
     * @param result
     * @return
     */
    public static Result logout(Result result) {
        return result.withSession(logout(result.session()));
    }

    /**
     * Log the current user out.
     *
     * @param session
     * @return
     */
    public static Session logout(Session session) {
        return session.removing(SESSION_USERNAME);
    }
}
