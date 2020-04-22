package domain.session;

import java.security.interfaces.RSAPublicKey;

public interface SessionRepository {

    RSAPublicKey getRSAPublicKey();

    Tokens createTokens(String userId);

    Tokens updateTokens(String refreshToken);

}
