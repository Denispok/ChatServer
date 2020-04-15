package app.api.user.registration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class RegistrationRequest {

    String login;
    String password;
}
