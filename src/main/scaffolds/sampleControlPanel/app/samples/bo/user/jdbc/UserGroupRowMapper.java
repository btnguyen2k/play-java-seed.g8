package samples.bo.user.jdbc;

import com.github.ddth.dao.jdbc.annotations.AnnotatedGenericRowMapper;
import com.github.ddth.dao.jdbc.annotations.ColumnAttribute;
import com.github.ddth.dao.jdbc.annotations.PrimaryKeyColumns;
import com.github.ddth.dao.jdbc.annotations.UpdateColumns;
import com.github.ddth.dao.utils.DaoException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import samples.bo.user.UserGroupBo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * RowMapper: ResultSet -> UserGroupBo
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
@ColumnAttribute(column = "gid", attr = "id", attrClass = String.class)
@ColumnAttribute(column = "gdata", attr = "data", attrClass = String.class)
@PrimaryKeyColumns({ "gid" })
@UpdateColumns({ "gdata" })
public class UserGroupRowMapper extends AnnotatedGenericRowMapper<UserGroupBo> {
    public final static UserGroupRowMapper INSTANCE = new UserGroupRowMapper();

    private Cache<String, String> cacheSQLs = CacheBuilder.newBuilder().build();

    private String strAllColumns = StringUtils.join(getAllColumns(), ",");
    private String strPkColumns = StringUtils.join(getPrimaryKeyColumns(), ",");
    private String strWherePkClause = StringUtils
            .join(Arrays.asList(getPrimaryKeyColumns()).stream().map(col -> col + "=?")
                    .toArray(String[]::new), " AND ");
    private String strUpdateSetClause = StringUtils
            .join(Arrays.asList(getUpdateColumns()).stream().map(col -> col + "=?")
                    .toArray(String[]::new), ",");

    /**
     * Generate SELECT statement to select a BO.
     *
     * <p>
     * The generated SQL will look like this {@code SELECT all-columns FROM table WHERE pk-1=? AND
     * pk-2=?...}
     * </p>
     *
     * @param tableName
     * @return
     */
    public String generateSqlSelect(String tableName) {
        try {
            return cacheSQLs.get("SELECT:" + tableName, () -> {
                return MessageFormat
                        .format("SELECT {2} FROM {0} WHERE {1}", tableName, strWherePkClause,
                                strAllColumns);
            });
        } catch (ExecutionException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Generate SELECT statement to SELECT all BOs, ordered by promary keys.
     *
     * <p>
     * The generated SQL will look like this {@code SELECT all-columns FROM table ORDER BY pk-1,
     * pk-2...}
     * </p>
     *
     * @param tableName
     * @return
     */
    public String generateSqlSelectAll(String tableName) {
        try {
            return cacheSQLs.get("SELECT-ALL:" + tableName, () -> {
                return MessageFormat
                        .format("SELECT {2} FROM {0} ORDER BY {1}", tableName, strPkColumns,
                                strAllColumns);
            });
        } catch (ExecutionException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Generate INSERT statement to insert a BO.
     *
     * <p>
     * The generated SQL will look like this {@code INSERT INTO table (all-columns) VALUES
     * (?,?,...)}
     * </p>
     *
     * @param tableName
     * @return
     */
    public String generateSqlInsert(String tableName) {
        try {
            return cacheSQLs.get("INSERT:" + tableName, () -> {
                return MessageFormat
                        .format("INSERT INTO {0} ({1}) VALUES ({2})", tableName, strAllColumns,
                                StringUtils.repeat("?", ",", getAllColumns().length));
            });
        } catch (ExecutionException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Generate DELETE statement to delete an existing BO.
     *
     * <p>
     * The generated SQL will look like this {@code DELETE FROM table WHERE pk-1=? AND pk-2=?...}
     * </p>
     *
     * @param tableName
     * @return
     */
    public String generateSqlDelete(String tableName) {
        try {
            return cacheSQLs.get("DELETE:" + tableName, () -> {
                return MessageFormat
                        .format("DELETE FROM {0} WHERE {1}", tableName, strWherePkClause);
            });
        } catch (ExecutionException e) {
            throw new DaoException(e);
        }
    }

    /**
     * Generate UPDATE statement to update an existing BO.
     *
     * <p>
     * The generated SQL will look like this {@code UPDATE table SET col1=?, col2=?...WHERE pk-1=?
     * AND pk-2=?...}
     * </p>
     *
     * @param tableName
     * @return
     */
    public String generateSqlUpdate(String tableName) {
        try {
            return cacheSQLs.get("UPDATE:" + tableName, () -> {
                return MessageFormat
                        .format("UPDATE {0} SET {2} WHERE {1}", tableName, strWherePkClause,
                                strUpdateSetClause);
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
