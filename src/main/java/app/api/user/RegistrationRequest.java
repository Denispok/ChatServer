package app.api.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@NoArgsConstructor
class RegistrationRequest {

    String login;
    String password;
}
