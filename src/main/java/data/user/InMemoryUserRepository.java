package data.user;

import app.error.exception.ConflictException;
import app.error.exception.ResourceNotFoundException;
import domain.user.UserRepository;
import domain.user.model.EditUser;
import domain.user.model.NewUser;
import domain.user.model.User;
import domain.user.model.UserLoginInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> USER_STORE = new ConcurrentHashMap<>();

    @Override
    public String create(NewUser newUser) {
        String id = UUID.randomUUID().toString();

        var userWithGivenLogin = USER_STORE.values().stream()
            .filter(u -> u.getLogin().equals(newUser.getLogin()))
            .findAny()
            .orElse(null);
        if (userWithGivenLogin != null) throw new ConflictException("This login already used");

        User user = User.builder()
            .id(id)
            .login(newUser.getLogin())
            .password(newUser.getPassword())
            .build();

        USER_STORE.put(id, user);
        return id;
    }

    /**
     * @return userId
     */
    @Override
    public String edit(String userId, EditUser user) {
        User currentUser = USER_STORE.get(userId);
        if (currentUser == null) throw new ResourceNotFoundException("User not found");

        User editedUser = User.builder().id(userId)
            .login(user.getLogin() != null ? user.getLogin() : currentUser.getLogin())
            .password(user.getPassword() != null ? user.getPassword() : currentUser.getPassword())
            .build();

        USER_STORE.put(editedUser.getId(), editedUser);
        return editedUser.getId();
    }

    /**
     * @return userId if login/password correct
     */
    @Override
    public String signIn(UserLoginInfo userLoginInfo) {
        var user = USER_STORE.values().stream()
            .filter(u -> u.getLogin().equals(userLoginInfo.getLogin()))
            .findAny()
            .orElse(null);

        if (user != null && user.getPassword().equals(userLoginInfo.getPassword())) {
            return user.getId();
        }

        throw new ResourceNotFoundException("User not found");
    }
}
