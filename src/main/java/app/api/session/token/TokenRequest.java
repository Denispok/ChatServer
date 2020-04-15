package app.api.session.token;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenRequest {

    public String refreshToken;
}
