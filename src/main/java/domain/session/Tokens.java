package domain.session;

import lombok.Data;

@Data
public class Tokens {

    final String refreshToken;
    final String jwt;

}
