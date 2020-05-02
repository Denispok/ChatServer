package domain.user;

import app.error.exception.ConflictException;
import app.error.exception.ResourceNotFoundException;
import domain.user.model.EditUser;
import domain.user.model.NewUser;
import domain.user.model.UserLoginInfo;

public interface UserRepository {

    String create(NewUser user) throws ConflictException;

    String edit(String userId, EditUser user) throws ResourceNotFoundException;

    String signIn(UserLoginInfo userLoginInfo) throws ResourceNotFoundException;
}
