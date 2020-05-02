package domain.session;

import app.error.exception.ResourceNotFoundException;
import domain.session.model.Tokens;

import java.security.interfaces.RSAPublicKey;

public interface SessionRepository {

    RSAPublicKey getRSAPublicKey();

    Tokens createTokens(String userId);

    Tokens updateTokens(String refreshToken) throws ResourceNotFoundException;
}
