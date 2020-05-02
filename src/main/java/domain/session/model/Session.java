package domain.session.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Session {

    String refreshToken;
    long expiresAt;
    String userId;

}
