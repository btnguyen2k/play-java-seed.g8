package samples.models;

import java.util.Collection;

import samples.bo.user.UserBo;

/**
 * User Model wraps around the BO, for use in view.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class UserModel extends UserBo {

    public static UserModel newInstance(UserBo bo) {
        UserModel model = new UserModel();
        model.fromMap(bo.toMap());
        return model;
    }

    public static UserModel[] newInstances(UserBo[] boList) {
        UserModel[] modelList = new UserModel[boList.length];
        for (int i = 0; i < modelList.length; i++) {
            modelList[i] = newInstance(boList[i]);
        }
        return modelList;
    }

    public static UserModel[] newInstances(Collection<UserBo> boList) {
        return newInstances(boList.toArray(UserBo.EMPTY_ARRAY));
    }

    public String urlEdit() {
        return samples.controllers.routes.SampleControlPanelController.editUser(getUsername())
                .toString();
    }

    public String urlDelete() {
        return samples.controllers.routes.SampleControlPanelController.deleteUser(getUsername())
                .toString();
    }

}
