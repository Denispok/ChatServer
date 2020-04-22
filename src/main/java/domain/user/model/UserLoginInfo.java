package domain.user.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserLoginInfo {

    String login;
    String password;
}
