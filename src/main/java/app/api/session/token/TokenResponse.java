package app.api.session.token;

import lombok.Value;

@Value
class TokenResponse {

    String refreshToken;
    String jwt;
}