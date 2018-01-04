package samples.utils;

import modules.registry.RegistryGlobal;
import play.mvc.Http.Session;
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
     * @param session
     * @return
     */
    public static UserBo currentUser(Session session) {
        String username = session.get(SESSION_USERNAME);
        IUserDao dao = RegistryGlobal.registry.getBean(IUserDao.class);
        return dao.getUser(username);
    }
}
