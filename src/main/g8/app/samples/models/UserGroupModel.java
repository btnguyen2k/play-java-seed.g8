package samples.models;

import java.util.Collection;

import samples.bo.user.UserGroupBo;

/**
 * Usergroup Model wraps around the BO, for use in view.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class UserGroupModel extends UserGroupBo {

    public static UserGroupModel newInstance(UserGroupBo bo) {
        UserGroupModel model = new UserGroupModel();
        model.fromMap(bo.toMap());
        return model;
    }

    public static UserGroupModel[] newInstances(UserGroupBo[] boList) {
        UserGroupModel[] modelList = new UserGroupModel[boList.length];
        for (int i = 0; i < modelList.length; i++) {
            modelList[i] = newInstance(boList[i]);
        }
        return modelList;
    }

    public static UserGroupModel[] newInstances(Collection<UserGroupBo> boList) {
        return newInstances(boList.toArray(UserGroupBo.EMPTY_ARRAY));
    }

    public String urlEdit() {
        return samples.controllers.routes.SampleControlPanelController.editUsergroup(getId());
    }

    public String urlDelete() {
        return "//TODO";
    }
}
