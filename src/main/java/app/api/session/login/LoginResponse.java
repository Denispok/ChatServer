package app.api.session.login;

import lombok.Value;

@Value
class LoginResponse {

    String refreshToken;
    String jwt;
}