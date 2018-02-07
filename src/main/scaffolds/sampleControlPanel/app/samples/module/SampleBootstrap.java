package samples.module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.google.inject.Provider;

import modules.registry.IRegistry;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import samples.bo.user.IUserDao;
import samples.bo.user.UserBo;
import samples.bo.user.UserGroupBo;
import samples.bo.user.jdbc.JdbcUserDao;
import samples.utils.SampleConstants;
import samples.utils.UserUtils;

/**
 * Bootstrap samples.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
public class SampleBootstrap {

    private Provider<IRegistry> registry;
    // private Application playApp;

    /**
     * {@inheritDoc}
     */
    @Inject
    public SampleBootstrap(ApplicationLifecycle lifecycle, Application playApp,
            Provider<IRegistry> registry) {
        // this.playApp = playApp;
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

    private boolean tableExists(Connection conn, String tableName) throws Exception {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(),
                new String[] { "TABLE" })) {
            return rs.next();
        }
    }

    private void init() throws Exception {
        DataSource ds = registry.get().getBean(DataSource.class);
        try (Connection conn = ds.getConnection()) {
            if (!tableExists(conn, JdbcUserDao.TABLE_USER)) {
                Logger.warn("Creating table [" + JdbcUserDao.TABLE_USER + "]...");
                conn.createStatement().execute("CREATE TABLE " + JdbcUserDao.TABLE_USER
                        + "(uname VARCHAR(64), udata CLOB, PRIMARY KEY (uname))");
            }

            if (!tableExists(conn, JdbcUserDao.TABLE_USERGROUP)) {
                Logger.warn("Creating table [" + JdbcUserDao.TABLE_USERGROUP + "]...");
                conn.createStatement().execute("CREATE TABLE " + JdbcUserDao.TABLE_USERGROUP
                        + "(gid VARCHAR(64), gdata CLOB, PRIMARY KEY (gid))");
            }
        }

        IUserDao dao = registry.get().getBean(IUserDao.class);
        UserGroupBo ugAdmin = dao.getUserGroup(SampleConstants.USERGROUP_ID_ADMIN);
        if (ugAdmin == null) {
            ugAdmin = UserGroupBo.newInstance(SampleConstants.USERGROUP_ID_ADMIN);
            ugAdmin.setDescription("Administrator User Group");
            dao.create(ugAdmin);
            Logger.warn("Created user group [" + SampleConstants.USERGROUP_ID_ADMIN + "]");
        }

        UserBo userAdmin = dao.getUser(SampleConstants.USERNAME_ADMIN);
        if (userAdmin == null) {
            String pwd = "secret";
            userAdmin = UserBo.newInstance(SampleConstants.USERNAME_ADMIN);
            userAdmin.setGroupId(SampleConstants.USERGROUP_ID_ADMIN).setEmail("admin@localhost")
                    .setFullname("Administrator").setPassword(UserUtils.encryptPassword(pwd));
            dao.create(userAdmin);
            Logger.warn("Created user [" + SampleConstants.USERGROUP_ID_ADMIN + "] with password ["
                    + pwd + "]");
        }
    }

    private void destroy() {
        // TODO
    }

}
