package domain.session;

public interface SessionRepository {

    Tokens createTokens(String userId);

    Tokens updateTokens(String refreshToken);

}
