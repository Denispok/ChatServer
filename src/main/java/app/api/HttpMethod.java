package app.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpMethod {
    POST("POST"),
    PUT("PUT"),
    GET("GET");

    public final String text;
}
