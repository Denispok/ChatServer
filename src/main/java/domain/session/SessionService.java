package domain.session;

import app.Configuration;
import domain.session.model.Tokens;
import domain.user.model.UserLoginInfo;
import lombok.AllArgsConstructor;

import java.security.interfaces.RSAPublicKey;

@AllArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public RSAPublicKey getRSAPublicKey() {
        return sessionRepository.getRSAPublicKey();
    }

    public Tokens signIn(UserLoginInfo userLoginInfo) {
        var userId = Configuration.getUserService().signIn(userLoginInfo);
        return sessionRepository.createTokens(userId);
    }

    public Tokens updateTokens(String refreshToken) {
        return sessionRepository.updateTokens(refreshToken);
    }
}
