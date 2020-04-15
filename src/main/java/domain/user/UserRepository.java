package domain.user;

public interface UserRepository {

    String create(NewUser user);

    String signIn(UserLoginInfo userLoginInfo);

}
