package data.session;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import domain.session.Session;
import domain.session.SessionRepository;
import domain.session.Tokens;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class InMemorySessionRepository implements SessionRepository {

    private static final RSAKeyStore rsaKeyStore = new RSAKeyStore();
    private static final Algorithm algorithm = Algorithm.RSA512(rsaKeyStore.getPublicKey(), rsaKeyStore.getPrivateKey());

    private static final Long JWT_EXPIRES_TIME_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final Long REFRESH_EXPIRES_TIME_MILLIS = TimeUnit.MINUTES.toMillis(2);
    private static final Map<String, Session> SESSION_STORE = new ConcurrentHashMap<>();

    @Override
    public RSAPublicKey getRSAPublicKey() {
        return rsaKeyStore.getPublicKey();
    }

    @Override
    public Tokens createTokens(String userId) {
        long refreshExpiresAt = System.currentTimeMillis() + REFRESH_EXPIRES_TIME_MILLIS;
        Session session = createSession(userId, refreshExpiresAt);
        SESSION_STORE.put(session.getRefreshToken(), session);

        String jwt = createJWT(userId);
        if (jwt == null) return null;

        return new Tokens(session.getRefreshToken(), jwt);
    }

    @Override
    public Tokens updateTokens(String refreshToken) {
        var currentSession = SESSION_STORE.get(refreshToken);
        if (currentSession == null) return null;
        if (currentSession.getExpiresAt() <= System.currentTimeMillis()) return null;

        Session newSession = createSession(currentSession.getUserId(), currentSession.getExpiresAt());
        SESSION_STORE.put(newSession.getRefreshToken(), newSession);
        SESSION_STORE.remove(currentSession.getRefreshToken());

        String jwt = createJWT(newSession.getUserId());
        if (jwt == null) return null;

        return new Tokens(newSession.getRefreshToken(), jwt);
    }

    private String createJWT(String userId) {
        String jwt;
        long jwtExpiresAt = System.currentTimeMillis() + JWT_EXPIRES_TIME_MILLIS;

        try {
            jwt = JWT.create()
                .withClaim("userId", userId)
                .withExpiresAt(new Date(jwtExpiresAt))
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
            return null;
        }

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
