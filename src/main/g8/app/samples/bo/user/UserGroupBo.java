package samples.bo.user;

import com.github.ddth.dao.BaseDataJsonFieldBo;

/**
 * BO: user group.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class UserGroupBo extends BaseDataJsonFieldBo {
    private final static String ATTR_ID = "id";

    public static UserGroupBo newInstance() {
        UserGroupBo bo = new UserGroupBo();
        bo.setData("{}");
        return bo;
    }

    public static UserGroupBo newInstance(String id) {
        UserGroupBo bo = newInstance();
        bo.setId(id);
        return bo;
    }

    private UserGroupBo() {
    }

    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public UserGroupBo setId(String value) {
        setAttribute(ATTR_ID, value != null ? value.trim().toLowerCase() : null);
        return this;
    }

    private final static String DATA_DESC = "desc";

    public String getDescription() {
        return getDataAttr(DATA_DESC, String.class);
    }

    public UserGroupBo setDescription(String value) {
        setDataAttr(DATA_DESC, value != null ? value.trim() : null);
        return this;
    }
}
