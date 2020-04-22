package app.api.user.edit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class EditRequest {

    String login;
    String password;
}
