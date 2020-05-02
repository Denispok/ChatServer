package data.session;

import app.error.exception.ResourceNotFoundException;
import app.error.exception.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import domain.session.SessionRepository;
import domain.session.model.Tokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SessionRepositoryTest {

    private static final Long JWT_EXPIRES_TIME_MILLIS = TimeUnit.SECONDS.toMillis(3);
    private static final Long REFRESH_EXPIRES_TIME_MILLIS = TimeUnit.SECONDS.toMillis(6);
    private SessionRepository sessionRepository;

    @BeforeEach
    void createRepository() {
        sessionRepository = new InMemorySessionRepository(JWT_EXPIRES_TIME_MILLIS, REFRESH_EXPIRES_TIME_MILLIS);
    }

    @Test
    void createTokensTest() {
        final var userId = UUID.randomUUID().toString();

        for (int i = 0; i < 5; i++) {
            var tokens = sessionRepository.createTokens(userId);
            assertNotNull(tokens);

            String userIdFromJWT;
            try {
                Algorithm algorithm = Algorithm.RSA512(sessionRepository.getRSAPublicKey(), null);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(tokens.getJwt());
                userIdFromJWT = decodedJWT.getClaim("userId").asString();
            } catch (JWTVerificationException exception) {
                throw new UnauthorizedException("JWT not valid");
            }
            assertEquals(userId, userIdFromJWT);
        }
    }

    @Test
    void updateTokensTest() {
        final var userId = UUID.randomUUID().toString();
        assertThrows(ResourceNotFoundException.class, () -> sessionRepository.updateTokens(userId));

        var tokens = sessionRepository.createTokens(userId);
        Tokens newTokens;

        for (int i = 0; i < 5; i++) {
            newTokens = sessionRepository.updateTokens(tokens.getRefreshToken());

            final Tokens previousTokens = tokens;
            assertThrows(ResourceNotFoundException.class, () -> sessionRepository.updateTokens(previousTokens.getRefreshToken()));
            tokens = newTokens;

            String userIdFromJWT;
            try {
                Algorithm algorithm = Algorithm.RSA512(sessionRepository.getRSAPublicKey(), null);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(tokens.getJwt());
                userIdFromJWT = decodedJWT.getClaim("userId").asString();
            } catch (JWTVerificationException exception) {
                throw new UnauthorizedException("JWT not valid");
            }
            assertEquals(userId, userIdFromJWT);
        }
    }

    @Test
    void tokensTimeTest() {
        final var userId = UUID.randomUUID().toString();
        var tokens = sessionRepository.createTokens(userId);

        sleep(JWT_EXPIRES_TIME_MILLIS + 1000);
        assertThrows(UnauthorizedException.class, () -> {
            try {
                Algorithm algorithm = Algorithm.RSA512(sessionRepository.getRSAPublicKey(), null);
                JWTVerifier verifier = JWT.require(algorithm).build();
                verifier.verify(tokens.getJwt());
            } catch (JWTVerificationException exception) {
                throw new UnauthorizedException("JWT not valid");
            }
        });

        var newTokens = sessionRepository.updateTokens(tokens.getRefreshToken());

        try {
            Algorithm algorithm = Algorithm.RSA512(sessionRepository.getRSAPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(newTokens.getJwt());
        } catch (JWTVerificationException exception) {
            throw new UnauthorizedException("JWT not valid");
        }

        sleep(REFRESH_EXPIRES_TIME_MILLIS + 1000);
        assertThrows(ResourceNotFoundException.class, () -> sessionRepository.updateTokens(newTokens.getRefreshToken()));
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread.sleep() failed");
        }
    }

}
