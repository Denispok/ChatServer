package domain.user;

import domain.user.model.EditUser;
import domain.user.model.NewUser;
import domain.user.model.UserLoginInfo;

public interface UserRepository {

    String create(NewUser user);

    String edit(String userId, EditUser user);

    String signIn(UserLoginInfo userLoginInfo);

}
