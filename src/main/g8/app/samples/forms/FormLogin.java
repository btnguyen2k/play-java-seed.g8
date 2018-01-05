package samples.forms;

import java.util.Arrays;

import forms.BaseForm;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;
import samples.bo.user.IUserDao;
import samples.bo.user.UserBo;
import samples.utils.UserUtils;

/**
 * Form example: login.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
@Validate
public class FormLogin extends BaseForm implements Validatable<ValidationError> {
    private String username, password;
    private UserBo user;

    public UserBo getUser() {
        return user;
    }

    /* Getters & Setters are required */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*----------------------------------------------------------------------*/

    /**
     * Form validation method.
     *
     * @return
     */
    public ValidationError validate() {
        IUserDao dao = getRegistry().getBean(IUserDao.class);
        user = dao.getUser(username);
        if (user == null) {
            return new ValidationError(null, "error.user_not_found", Arrays.asList(username));
        }
        if (!UserUtils.authenticate(user, password)) {
            return new ValidationError(null, "error.login_failed");
        }

        return null;
    }
}
