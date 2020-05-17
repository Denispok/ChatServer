package data.session;

import app.error.exception.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import domain.session.SessionRepository;
import domain.session.model.Session;
import domain.session.model.Tokens;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository implements SessionRepository {

    private final RSAKeyStore rsaKeyStore = new RSAKeyStore();
    private final Algorithm algorithm = Algorithm.RSA512(rsaKeyStore.getPublicKey(), rsaKeyStore.getPrivateKey());

    private final Long JWT_EXPIRES_TIME_MILLIS;
    private final Long REFRESH_EXPIRES_TIME_MILLIS;
    private final Map<String, Session> SESSION_STORE = new ConcurrentHashMap<>();

    public InMemorySessionRepository(Long jwtExpiresTimeMillis, Long refreshExpiresTimeMillis) {
        JWT_EXPIRES_TIME_MILLIS = jwtExpiresTimeMillis;
        REFRESH_EXPIRES_TIME_MILLIS = refreshExpiresTimeMillis;
    }

    @Override
    public RSAPublicKey getRSAPublicKey() {
        return rsaKeyStore.getPublicKey();
    }

    @Override
    public Tokens createTokens(String userId) {
        long refreshExpiresAt = System.currentTimeMillis() + REFRESH_EXPIRES_TIME_MILLIS;
        Session session = createSession(userId, refreshExpiresAt);
        String jwt = createJWT(userId);

        SESSION_STORE.put(session.getRefreshToken(), session);
        return new Tokens(session.getRefreshToken(), jwt);
    }

    @Override
    public Tokens updateTokens(String refreshToken) {
        var currentSession = SESSION_STORE.get(refreshToken);
        if (currentSession == null) throw new UnauthorizedException("Refresh token not found");
        if (currentSession.getExpiresAt() <= System.currentTimeMillis()) throw new UnauthorizedException("Token is expired");

        Session newSession = createSession(currentSession.getUserId(), currentSession.getExpiresAt());
        String jwt = createJWT(newSession.getUserId());

        SESSION_STORE.put(newSession.getRefreshToken(), newSession);
        SESSION_STORE.remove(currentSession.getRefreshToken());

        return new Tokens(newSession.getRefreshToken(), jwt);
    }

    private String createJWT(String userId) throws JWTCreationException {
        long jwtExpiresAt = System.currentTimeMillis() + JWT_EXPIRES_TIME_MILLIS;

        String jwt = JWT.create()
            .withClaim("userId", userId)
            .withExpiresAt(new Date(jwtExpiresAt))
            .sign(algorithm);

        return jwt;
    }

    private Session createSession(String userId, long expiresAt) {
        String refreshToken = UUID.randomUUID().toString();

        return Session.builder()
            .refreshToken(refreshToken)
            .userId(userId)
            .expiresAt(expiresAt)
            .build();
    }
}
