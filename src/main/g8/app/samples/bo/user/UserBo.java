package samples.bo.user;

import java.util.Date;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.dao.BaseDataJsonFieldBo;

/**
 * BO: user.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class UserBo extends BaseDataJsonFieldBo {
    private final static String ATTR_USERNAME = "username";

    public static UserBo newInstance() {
        UserBo bo = new UserBo();
        bo.setData("{}");
        bo.setTimestampCreated(new Date());
        return bo;
    }

    public static UserBo newInstance(String username) {
        UserBo bo = newInstance();
        bo.setUsername(username);
        return bo;
    }

    private UserBo() {
    }

    public String getUsername() {
        return getAttribute(ATTR_USERNAME, String.class);
    }

    public UserBo setUsername(String value) {
        setAttribute(ATTR_USERNAME, value != null ? value.trim().toLowerCase() : null);
        return this;
    }

    private final static String DATA_TIMESTAMP_CREATED = "tcreated";

    public Date getTimestampCreated() {
        return getDataAttr(DATA_TIMESTAMP_CREATED, Date.class);
    }

    public String getTimestampCreatedStr() {
        Date value = getTimestampCreated();
        return value != null ? DateFormatUtils.toString(value, DateFormatUtils.DF_ISO8601)
                : "[null]";
    }

    public UserBo setTimestampCreated(Date value) {
        setDataAttr(DATA_TIMESTAMP_CREATED, value);
        return this;
    }

    private final static String DATA_GROUP_ID = "gid";

    public String getGroupId() {
        return getDataAttr(DATA_GROUP_ID, String.class);
    }

    public UserBo setGroupId(String value) {
        setDataAttr(DATA_GROUP_ID, value != null ? value.trim().toLowerCase() : null);
        return this;
    }

    private final static String DATA_FULLNAME = "fullname";

    public String getFullname() {
        return getDataAttr(DATA_FULLNAME, String.class);
    }

    public UserBo setFullname(String value) {
        setDataAttr(DATA_FULLNAME, value != null ? value.trim() : null);
        return this;
    }

    private final static String DATA_PASSWORD = "pwd";

    public String getPassword() {
        return getDataAttr(DATA_PASSWORD, String.class);
    }

    public UserBo setPassword(String value) {
        setDataAttr(DATA_PASSWORD, value != null ? value.trim() : null);
        return this;
    }

    private final static String DATA_EMAIL = "email";

    public String getEmail() {
        return getDataAttr(DATA_EMAIL, String.class);
    }

    public UserBo setEmail(String value) {
        setDataAttr(DATA_EMAIL, value != null ? value.trim().toLowerCase() : null);
        return this;
    }
}
