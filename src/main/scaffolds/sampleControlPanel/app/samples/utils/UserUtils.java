package samples.utils;

import org.apache.commons.lang3.StringUtils;

import com.github.ddth.commons.utils.HashUtils;

import samples.bo.user.IUserDao;
import samples.bo.user.UserBo;
import samples.bo.user.UserGroupBo;

/**
 * User-related helper class.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class UserUtils {
    /**
     * Encrypt a raw password.
     *
     * @param rawPassword
     * @return
     */
    public static String encryptPassword(String rawPassword) {
        /*
         * Caution: this method's implementation is for demo purpose only. In
         * production, hashing password using MD5 without salt is NOT secure!
         */
        return rawPassword != null ? HashUtils.md5(rawPassword.trim()) : null;
    }

    /**
     * Authenticate a user.
     *
     * @param user
     * @param
     * @return
     */
    public static boolean authenticate(UserBo user, String inputPassword) {
        if (user == null || inputPassword == null) {
            return false;
        }
        return StringUtils.equalsAnyIgnoreCase(user.getPassword(), encryptPassword(inputPassword));
    }

    /**
     * Get all available users.
     *
     * @return
     */
    public static UserBo[] allUsers() {
        return RegistryGlobal.registry.getBean(IUserDao.class).getAllUsers()
                .toArray(UserBo.EMPTY_ARRAY);
    }

    /**
     * Count number of user accounts.
     *
     * @return
     */
    public static int countUsers() {
        return RegistryGlobal.registry.getBean(IUserDao.class).getAllUsers().size();
    }

    /**
     * Get all available user groups.
     *
     * @return
     */
    public static UserGroupBo[] allUsergroups() {
        return RegistryGlobal.registry.getBean(IUserDao.class).getAllUserGroups()
                .toArray(UserGroupBo.EMPTY_ARRAY);
    }

    /**
     * Count number of user groups.
     *
     * @return
     */
    public static int countUsergroups() {
        return RegistryGlobal.registry.getBean(IUserDao.class).getAllUserGroups().size();
    }
}
