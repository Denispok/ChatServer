package domain.session;

import app.Configuration;
import domain.user.UserLoginInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Tokens signIn(UserLoginInfo userLoginInfo) {
        var userId = Configuration.getUserService().signIn(userLoginInfo);
        if (userId == null) return null;

        return sessionRepository.createTokens(userId);
    }

    public Tokens updateTokens(String refreshToken) {
        return sessionRepository.updateTokens(refreshToken);
    }
}
