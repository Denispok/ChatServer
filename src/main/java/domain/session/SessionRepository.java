package domain.session;

import app.error.exception.UnauthorizedException;
import domain.session.model.Tokens;

import java.security.interfaces.RSAPublicKey;

public interface SessionRepository {

    RSAPublicKey getRSAPublicKey();

    Tokens createTokens(String userId);

    Tokens updateTokens(String refreshToken) throws UnauthorizedException;
}
