package samples.forms;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import forms.BaseForm;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;
import samples.bo.user.IUserDao;
import samples.bo.user.UserGroupBo;

/**
 * Form example: create/edit user group.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
@Validate
public class FormCreateEditUsergroup extends BaseForm implements Validatable<ValidationError> {

    public static FormCreateEditUsergroup newInstance(UserGroupBo bo) {
        FormCreateEditUsergroup form = new FormCreateEditUsergroup();
        form.id = bo.getId();
        form.description = bo.getDescription();
        form.editId = form.id;
        return form;
    }

    private String editId = "";
    private String id = "", description = "";
    private UserGroupBo usergroup;

    public UserGroupBo getUsergroup() {
        return usergroup;
    }

    /* Getters & Setters are required */

    public String getEditId() {
        return editId;
    }

    public void setEditId(String editId) {
        this.editId = editId != null ? editId.trim().toLowerCase() : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim().toLowerCase() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
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

        if (StringUtils.isBlank(id)) {
            return new ValidationError("", "error.usergroup.empty_id");
        }

        UserGroupBo newUsergroup = dao.getUserGroup(id);
        if (newUsergroup != null && !StringUtils.equals(id, editId)) {
            return new ValidationError("", "error.usergroup.exists", Collections.singletonList(id));
        }

        return null;
    }
}
