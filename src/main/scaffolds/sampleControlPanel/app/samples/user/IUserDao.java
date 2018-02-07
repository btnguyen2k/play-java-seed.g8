package samples.bo.user;

import java.util.Collection;

import com.github.ddth.dao.utils.DaoResult;

/**
 * API to access user/usergroup storage.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public interface IUserDao {
    /**
     * Get a user by username.
     *
     * @param username
     * @return
     */
    UserBo getUser(String username);

    /**
     * Get all available users.
     *
     * @return
     */
    Collection<UserBo> getAllUsers();

    /**
     * Create a new user.
     *
     * @param user
     * @return
     */
    DaoResult create(UserBo user);

    /**
     * Delete an existing user.
     *
     * @param user
     * @return
     */
    DaoResult delete(UserBo user);

    /**
     * Update an existing user.
     *
     * @param user
     * @return
     */
    DaoResult update(UserBo user);

    /**
     * Create new or update an existing user.
     *
     * @param user
     * @return
     */
    DaoResult createOrUpdate(UserBo user);

    /**
     * Get a user group by id.
     *
     * @param id
     * @return
     */
    UserGroupBo getUserGroup(String id);

    /**
     * Get all available user groups.
     *
     * @return
     */
    Collection<UserGroupBo> getAllUserGroups();

    /**
     * Create a new user group.
     *
     * @param ug
     * @return
     */
    DaoResult create(UserGroupBo ug);

    /**
     * Delete an existing user group.
     *
     * @param ug
     * @return
     */
    DaoResult delete(UserGroupBo ug);

    /**
     * Update an existing user group.
     *
     * @param ug
     * @return
     */
    DaoResult update(UserGroupBo ug);

    /**
     * Create new or update an existing user group.
     *
     * @param ug
     * @return
     */
    DaoResult createOrUpdate(UserGroupBo ug);
}
