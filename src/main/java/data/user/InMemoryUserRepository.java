package data.user;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import domain.user.NewUser;
import domain.user.User;
import domain.user.UserRepository;

public class InMemoryUserRepository implements UserRepository {

    private static final Map<String, User> USERS_STORE = new ConcurrentHashMap<>();

    @Override
    public String create(NewUser newUser) {
        String id = UUID.randomUUID().toString();
        User user = User.builder()
            .id(id)
            .login(newUser.getLogin())
            .password(newUser.getPassword())
            .build();
        USERS_STORE.put(id, user);

        return id;
    }
}
