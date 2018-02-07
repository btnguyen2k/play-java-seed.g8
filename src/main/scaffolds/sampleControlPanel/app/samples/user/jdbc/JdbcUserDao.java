package samples.bo.user.jdbc;

import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.ddth.dao.jdbc.BaseJdbcDao;
import com.github.ddth.dao.utils.DaoResult;
import com.github.ddth.dao.utils.DaoResult.DaoOperationStatus;
import com.github.ddth.dao.utils.DuplicatedValueException;

import samples.bo.user.IUserDao;
import samples.bo.user.UserBo;
import samples.bo.user.UserGroupBo;

/**
 * JDBC implementation of {@link IUserDao}.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class JdbcUserDao extends BaseJdbcDao implements IUserDao {

    public final static String TABLE_USER = "pjt_user";
    public final static String TABLE_USERGROUP = "pjt_usergroup";

    public JdbcUserDao init() {
        super.init();

        SQL_SELECT_USER = UserRowMapper.INSTANCE.generateSqlSelect(TABLE_USER);
        SQL_SELECT_ALL_USERS = UserRowMapper.INSTANCE.generateSqlSelectAll(TABLE_USER);
        SQL_INSERT_USER = UserRowMapper.INSTANCE.generateSqlInsert(TABLE_USER);
        SQL_DELETE_USER = UserRowMapper.INSTANCE.generateSqlDelete(TABLE_USER);
        SQL_UPDATE_USER = UserRowMapper.INSTANCE.generateSqlUpdate(TABLE_USER);

        SQL_SELECT_USERGROUP = UserGroupRowMapper.INSTANCE.generateSqlSelect(TABLE_USERGROUP);
        SQL_SELECT_ALL_USERGROUPS = UserGroupRowMapper.INSTANCE
                .generateSqlSelectAll(TABLE_USERGROUP);
        SQL_INSERT_USERGROUP = UserGroupRowMapper.INSTANCE.generateSqlInsert(TABLE_USERGROUP);
        SQL_DELETE_USERGROUP = UserGroupRowMapper.INSTANCE.generateSqlDelete(TABLE_USERGROUP);
        SQL_UPDATE_USERGROUP = UserGroupRowMapper.INSTANCE.generateSqlUpdate(TABLE_USERGROUP);

        return this;
    }

    private String SQL_SELECT_USER, SQL_SELECT_ALL_USERS, SQL_INSERT_USER, SQL_DELETE_USER,
            SQL_UPDATE_USER;
    private String SQL_SELECT_USERGROUP, SQL_SELECT_ALL_USERGROUPS, SQL_INSERT_USERGROUP,
            SQL_DELETE_USERGROUP, SQL_UPDATE_USERGROUP;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserBo getUser(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return executeSelectOne(UserRowMapper.INSTANCE, SQL_SELECT_USER, username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserBo> getAllUsers() {
        return executeSelect(UserRowMapper.INSTANCE, SQL_SELECT_ALL_USERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult create(UserBo user) {
        if (user == null) {
            return null;
        }
        try {
            int numRows = execute(SQL_INSERT_USER, UserRowMapper.INSTANCE.valuesForColumns(user,
                    UserRowMapper.INSTANCE.getAllColumns()));
            DaoResult result = numRows > 0 ? new DaoResult(DaoOperationStatus.SUCCESSFUL, user)
                    : new DaoResult(DaoOperationStatus.ERROR);
            return result;
        } catch (DuplicatedValueException dke) {
            return new DaoResult(DaoOperationStatus.DUPLICATED_VALUE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult delete(UserBo user) {
        if (user == null) {
            return new DaoResult(DaoOperationStatus.NOT_FOUND);
        }
        int numRows = execute(SQL_DELETE_USER, UserRowMapper.INSTANCE.valuesForColumns(user,
                UserRowMapper.INSTANCE.getPrimaryKeyColumns()));
        DaoResult result = numRows > 0 ? new DaoResult(DaoOperationStatus.SUCCESSFUL, user)
                : new DaoResult(DaoOperationStatus.NOT_FOUND);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult update(UserBo user) {
        if (user == null) {
            return new DaoResult(DaoOperationStatus.NOT_FOUND);
        }
        try {
            String[] bindColumns = ArrayUtils.addAll(UserRowMapper.INSTANCE.getUpdateColumns(),
                    UserRowMapper.INSTANCE.getPrimaryKeyColumns());
            int numRows = execute(SQL_UPDATE_USER,
                    UserRowMapper.INSTANCE.valuesForColumns(user, bindColumns));
            DaoResult result = numRows > 0 ? new DaoResult(DaoOperationStatus.SUCCESSFUL, user)
                    : new DaoResult(DaoOperationStatus.NOT_FOUND);
            return result;
        } catch (DuplicatedValueException dke) {
            return new DaoResult(DaoOperationStatus.DUPLICATED_VALUE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult createOrUpdate(UserBo user) {
        if (user == null) {
            return null;
        }
        DaoResult result = update(user);
        if (result.getStatus() == DaoOperationStatus.NOT_FOUND) {
            result = create(user);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserGroupBo getUserGroup(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return executeSelectOne(UserGroupRowMapper.INSTANCE, SQL_SELECT_USERGROUP, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserGroupBo> getAllUserGroups() {
        return executeSelect(UserGroupRowMapper.INSTANCE, SQL_SELECT_ALL_USERGROUPS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult create(UserGroupBo ug) {
        if (ug == null) {
            return null;
        }
        try {
            int numRows = execute(SQL_INSERT_USERGROUP, UserGroupRowMapper.INSTANCE
                    .valuesForColumns(ug, UserGroupRowMapper.INSTANCE.getAllColumns()));
            DaoResult result = numRows > 0 ? new DaoResult(DaoOperationStatus.SUCCESSFUL, ug)
                    : new DaoResult(DaoOperationStatus.ERROR);
            return result;
        } catch (DuplicatedValueException dke) {
            return new DaoResult(DaoOperationStatus.DUPLICATED_VALUE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult delete(UserGroupBo ug) {
        if (ug == null) {
            return new DaoResult(DaoOperationStatus.NOT_FOUND);
        }
        int numRows = execute(SQL_DELETE_USERGROUP, UserGroupRowMapper.INSTANCE.valuesForColumns(ug,
                UserGroupRowMapper.INSTANCE.getPrimaryKeyColumns()));
        DaoResult result = numRows > 0 ? new DaoResult(DaoOperationStatus.SUCCESSFUL, ug)
                : new DaoResult(DaoOperationStatus.NOT_FOUND);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult update(UserGroupBo ug) {
        if (ug == null) {
            return new DaoResult(DaoOperationStatus.NOT_FOUND);
        }
        try {
            String[] bindColumns = ArrayUtils.addAll(UserGroupRowMapper.INSTANCE.getUpdateColumns(),
                    UserGroupRowMapper.INSTANCE.getPrimaryKeyColumns());
            int numRows = execute(SQL_UPDATE_USERGROUP,
                    UserGroupRowMapper.INSTANCE.valuesForColumns(ug, bindColumns));
            DaoResult result = numRows > 0 ? new DaoResult(DaoOperationStatus.SUCCESSFUL, ug)
                    : new DaoResult(DaoOperationStatus.NOT_FOUND);
            return result;
        } catch (DuplicatedValueException dke) {
            return new DaoResult(DaoOperationStatus.DUPLICATED_VALUE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DaoResult createOrUpdate(UserGroupBo ug) {
        if (ug == null) {
            return null;
        }
        DaoResult result = update(ug);
        if (result.getStatus() == DaoOperationStatus.NOT_FOUND) {
            result = create(ug);
        }
        return result;
    }

}
