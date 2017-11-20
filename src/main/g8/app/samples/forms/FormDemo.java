package samples.forms;

import org.apache.commons.lang3.StringUtils;

import forms.BaseForm;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;

/**
 * Form example.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
@Validate
public class FormDemo extends BaseForm implements Validatable<ValidationError> {
    private String email;
    private String fullname;
    private int dob, mob, yob;

    /* Getters & Setters are required */

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getDob() {
        return dob;
    }

    public void setDob(int dob) {
        this.dob = dob;
    }

    public int getMob() {
        return mob;
    }

    public void setMob(int mob) {
        this.mob = mob;
    }

    public int getYob() {
        return yob;
    }

    public void setYob(int yob) {
        this.yob = yob;
    }

    /*----------------------------------------------------------------------*/

    /**
     * Form validation method.
     * 
     * @return
     */
    public ValidationError validate() {
        if (StringUtils.isBlank(email)) {
            /*
             * Error for field "email" and the error message is resolved from language files.
             */
            return new ValidationError(null, "error.empty_email");
        }
        if (!email.matches("^\\\\s*[0-9a-zA-Z_\\\\-\\\\.]+\\\\@[0-9a-zA-Z_\\\\-\\\\.]+\\\\s*\$")) {
            /*
             * Error for field "email" and the error message is custom text
             */
            return new ValidationError(null, "Invalid email address!");
        }
        if (StringUtils.isBlank(fullname)) {
            /*
             * Error for field "fullname" and the error message is resolved from language files.
             */
            return new ValidationError(null, "error.empty_fullname");
        }
        if (dob < 1 || dob > 31) {
            /*
             * Error for field "dob" and the error message is resolved from language files.
             */
            return new ValidationError(null, "error.invalid_dob");
        }
        if (mob < 1 || mob > 12) {
            /*
             * Error for field "mob" and the error message is resolved from language files.
             */
            return new ValidationError(null, "error.invalid_mob");
        }
        if (yob < 1970 || yob > 2070) {
            /*
             * Error for field "yob" and the error message is resolved from language files.
             */
            return new ValidationError(null, "error.invalid_yob");
        }

        return null;
    }
}
