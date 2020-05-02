package domain.user;

import app.Configuration;
import app.error.exception.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import domain.user.model.EditUser;
import domain.user.model.NewUser;
import domain.user.model.UserLoginInfo;

import java.security.interfaces.RSAPublicKey;

public class UserService {

    private final UserRepository userRepository;

    private RSAPublicKey rsaPublicKey;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private RSAPublicKey getRSAPublicKey() {
        if (rsaPublicKey == null) rsaPublicKey = Configuration.getSessionService().getRSAPublicKey();
        return rsaPublicKey;
    }

    public String create(NewUser user) {
        return userRepository.create(user);
    }

    public String edit(String jwt, EditUser user) {
        String userId;
        try {
            Algorithm algorithm = Algorithm.RSA512(getRSAPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(jwt);
            userId = decodedJWT.getClaim("userId").asString();
        } catch (JWTVerificationException exception) {
            throw new UnauthorizedException("JWT not valid");
        }
        return userRepository.edit(userId, user);
    }

    public String signIn(UserLoginInfo userLoginInfo) {
        return userRepository.signIn(userLoginInfo);
    }

}
