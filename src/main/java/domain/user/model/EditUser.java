package domain.user.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EditUser {

    String login;
    String password;
}
