package samples.controllers;

import com.github.ddth.dao.utils.DaoResult;
import com.github.ddth.dao.utils.DaoResult.DaoOperationStatus;

import controllers.BasePageController;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import samples.bo.user.IUserDao;
import samples.bo.user.UserGroupBo;
import samples.compositions.AuthRequired;
import samples.forms.FormCreateEditUsergroup;
import samples.models.UserGroupModel;
import samples.utils.SampleConstants;

/**
 * Sample control panel controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
@AuthRequired(loginCall = "samples.controllers.routes:SampleController:login", usergroups = {
        SampleConstants.USERGROUP_ID_ADMIN })
public class SampleControlPanelController extends BasePageController {

    public final static String VIEW_HOME = "vsamples.home";

    /**
     * Handle GET /<context>/cp
     */
    public Result home() throws Exception {
        return ok(render(VIEW_HOME));
    }

    public final static String VIEW_USERGROUPS = "vsamples.usergroups";

    /**
     * Handle GET /<context>/cp/usergroups
     */
    public Result usergroups() throws Exception {
        IUserDao dao = getRegistry().getBean(IUserDao.class);
        UserGroupModel[] allUsergroups = UserGroupModel.newInstances(dao.getAllUserGroups());

        // Note: cast to Object to make sure the whole array is treated as a
        // single parameter value!
        return ok(render(VIEW_USERGROUPS, (Object) allUsergroups));
    }

    public final static String VIEW_CREATE_USERGROUP = "vsamples.create_usergroup";

    /**
     * Handle GET /<context>/cp/createUsergroup
     */
    public Result createUsergroup() throws Exception {
        Form<FormCreateEditUsergroup> form = formFactory.form(FormCreateEditUsergroup.class);
        Html html = render(VIEW_CREATE_USERGROUP, form);
        return ok(html);
    }

    /**
     * Handle POST /<context>/cp/createUsergroup
     */
    public Result createUsergroupSubmit() throws Exception {
        Form<FormCreateEditUsergroup> form = formFactory.form(FormCreateEditUsergroup.class)
                .bindFromRequest(request());
        if (form.hasErrors()) {
            Html html = render(VIEW_CREATE_USERGROUP, form);
            return ok(html);
        }
        FormCreateEditUsergroup formData = form.get();
        UserGroupBo bo = UserGroupBo.newInstance(formData.getId())
                .setDescription(formData.getDescription());
        IUserDao dao = getRegistry().getBean(IUserDao.class);
        DaoResult result = dao.create(bo);
        if (result.getStatus() == DaoOperationStatus.SUCCESSFUL) {
            return responseRedirect(
                    samples.controllers.routes.SampleControlPanelController.usergroups(),
                    VIEW_USERGROUPS, calcMessages().at("msg.create_usergroup.done", bo.getId()));
        } else {
            return responseRedirect(
                    samples.controllers.routes.SampleControlPanelController.usergroups(),
                    VIEW_USERGROUPS, SampleConstants.FLASH_MSG_PREFIX_WARNING
                            + calcMessages().at("msg.create_usergroup.failed", bo.getId()));
        }
    }

    public final static String VIEW_EDIT_USERGROUP = "vsamples.edit_usergroup";

    /**
     * Handle GET /<context>/cp/editUsergroup?id=<usergroup-id>
     */
    public Result editUsergroup(String id) throws Exception {
        IUserDao dao = getRegistry().getBean(IUserDao.class);
        UserGroupBo bo = dao.getUserGroup(id);
        if (bo == null) {
            return responseRedirect(
                    samples.controllers.routes.SampleControlPanelController.usergroups(),
                    VIEW_USERGROUPS, SampleConstants.FLASH_MSG_PREFIX_ERROR
                            + calcMessages().at("error.usergroup.not_found", id));
        }
        Form<FormCreateEditUsergroup> form = formFactory.form(FormCreateEditUsergroup.class)
                .fill(FormCreateEditUsergroup.newInstance(bo));
        Html html = render(VIEW_EDIT_USERGROUP, form);
        return ok(html);
    }

    /**
     * Handle POST /<context>/cp/editUsergroup?id=<usergroup-id>
     */
    public Result editUsergroupSubmit(String id) throws Exception {
        IUserDao dao = getRegistry().getBean(IUserDao.class);
        UserGroupBo bo = dao.getUserGroup(id);
        if (bo == null) {
            return responseRedirect(
                    samples.controllers.routes.SampleControlPanelController.usergroups(),
                    VIEW_USERGROUPS, SampleConstants.FLASH_MSG_PREFIX_ERROR
                            + calcMessages().at("error.usergroup.not_found", id));
        }
        Form<FormCreateEditUsergroup> form = formFactory.form(FormCreateEditUsergroup.class)
                .bindFromRequest(request());
        if (form.hasErrors()) {
            Html html = render(VIEW_EDIT_USERGROUP, form);
            return ok(html);
        }
        FormCreateEditUsergroup formData = form.get();
        bo.setDescription(formData.getDescription());
        DaoResult result = dao.update(bo);
        if (result.getStatus() == DaoOperationStatus.SUCCESSFUL) {
            return responseRedirect(
                    samples.controllers.routes.SampleControlPanelController.usergroups(),
                    VIEW_USERGROUPS, calcMessages().at("msg.edit_usergroup.done", bo.getId()));
        } else {
            return responseRedirect(
                    samples.controllers.routes.SampleControlPanelController.usergroups(),
                    VIEW_USERGROUPS, SampleConstants.FLASH_MSG_PREFIX_WARNING
                            + calcMessages().at("msg.edit_usergroup.failed", bo.getId()));
        }
    }
}
