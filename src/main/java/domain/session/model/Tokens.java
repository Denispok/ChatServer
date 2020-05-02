package domain.session.model;

import lombok.Data;

@Data
public class Tokens {

    final String refreshToken;
    final String jwt;

}
