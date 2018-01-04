package samples.forms;

import forms.BaseForm;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;

/**
 * Form example: login.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
@Validate
public class FormLogin extends BaseForm implements Validatable<ValidationError> {
    private String username, password;

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
        // if (StringUtils.isBlank(email)) {
        // /*
        // * Error for field "email" and the error message is resolved from
        // * language files.
        // */
        // return new ValidationError(null, "error.empty_email");
        // }
        // if
        // (!email.matches("^\\s*[0-9a-zA-Z_\\-\\.]+\\@[0-9a-zA-Z_\\-\\.]+\\s*$"))
        // {
        // /*
        // * Error for field "email" and the error message is custom text
        // */
        // return new ValidationError(null, "Invalid email address!");
        // }
        // if (StringUtils.isBlank(fullname)) {
        // /*
        // * Error for field "fullname" and the error message is resolved from
        // * language files.
        // */
        // return new ValidationError(null, "error.empty_fullname");
        // }
        // if (dob < 1 || dob > 31) {
        // /*
        // * Error for field "dob" and the error message is resolved from
        // * language files.
        // */
        // return new ValidationError(null, "error.invalid_dob");
        // }
        // if (mob < 1 || mob > 12) {
        // /*
        // * Error for field "mob" and the error message is resolved from
        // * language files.
        // */
        // return new ValidationError(null, "error.invalid_mob");
        // }
        // if (yob < 1970 || yob > 2070) {
        // /*
        // * Error for field "yob" and the error message is resolved from
        // * language files.
        // */
        // return new ValidationError(null, "error.invalid_yob");
        // }

        return null;
    }
}
