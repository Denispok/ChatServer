package domain.user.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewUser {

    String login;
    String password;
}
