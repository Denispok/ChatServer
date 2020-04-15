package data.user;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import domain.user.UserLoginInfo;
import domain.user.NewUser;
import domain.user.User;
import domain.user.UserRepository;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> USER_STORE = new ConcurrentHashMap<>();

    @Override
    public String create(NewUser newUser) {
        String id = UUID.randomUUID().toString();
        User user = User.builder()
            .id(id)
            .login(newUser.getLogin())
            .password(newUser.getPassword())
            .build();
        USER_STORE.put(id, user);

        return id;
    }

    /**
     * @return userId if login/password correct, null otherwise.
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

        return null;
    }
}
