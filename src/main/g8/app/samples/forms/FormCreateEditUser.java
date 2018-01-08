package samples.forms;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import forms.BaseForm;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;
import samples.bo.user.IUserDao;
import samples.bo.user.UserBo;
import samples.bo.user.UserGroupBo;

/**
 * Form example: create/edit user.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
@Validate
public class FormCreateEditUser extends BaseForm implements Validatable<ValidationError> {

    public static FormCreateEditUser newInstance(UserBo bo) {
        FormCreateEditUser form = new FormCreateEditUser();
        form.username = bo.getUsername();
        form.fullname = bo.getFullname();
        form.email = bo.getEmail();
        form.groupId = bo.getGroupId();
        form.editUsername = form.username;
        return form;
    }

    private String editUsername = "";
    private String username = "", fullname = "", email = "", password = "", confirmedPassword = "",
            groupId = "";
    private UserGroupBo usergroup;

    public UserGroupBo getUsergroup() {
        return usergroup;
    }

    /* Getters & Setters are required */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim().toLowerCase() : null;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname != null ? fullname.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword != null ? confirmedPassword.trim() : null;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId != null ? groupId.trim().toLowerCase() : null;
    }
    /*----------------------------------------------------------------------*/

    /**
     * Form validation method.
     *
     * @return
     */
    @Override
    public ValidationError validate() {
        IUserDao dao = getRegistry().getBean(IUserDao.class);

        if (StringUtils.isBlank(username)) {
            return new ValidationError("", "error.user.empty_username");
        }
        if (StringUtils.isBlank(password)) {
            return new ValidationError("", "error.user.empty_password");
        }
        if (!StringUtils.equals(password, confirmedPassword)) {
            return new ValidationError("", "error.user.mismatched_password");
        }

        usergroup = dao.getUserGroup(groupId);
        if (usergroup == null) {
            return new ValidationError("", "error.usergroup.not_found",
                    Collections.singletonList(groupId != null ? groupId : ""));
        }

        UserBo newUser = dao.getUser(username);
        if (newUser != null && !StringUtils.equals(username, editUsername)) {
            return new ValidationError("", "error.user.exists",
                    Collections.singletonList(username));
        }

        return null;
    }

}
