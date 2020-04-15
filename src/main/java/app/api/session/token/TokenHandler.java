package app.api.session.token;

import app.api.Constants;
import app.api.Handler;
import app.api.ResponseEntity;
import app.api.StatusCode;
import app.errors.ApplicationExceptions;
import app.errors.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import domain.session.SessionService;
import domain.session.Tokens;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TokenHandler extends Handler {

    private final SessionService sessionService;

    public TokenHandler(SessionService sessionService, ObjectMapper objectMapper,
                        GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.sessionService = sessionService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        byte[] response;
        if ("POST".equals(exchange.getRequestMethod())) {
            ResponseEntity<TokenResponse> responseEntity = doPost(exchange.getRequestBody());
            exchange.getResponseHeaders().putAll(responseEntity.getHeaders());
            exchange.sendResponseHeaders(responseEntity.getStatusCode().getCode(), 0);
            response = super.writeResponse(responseEntity.getBody());
        } else {
            throw ApplicationExceptions.methodNotAllowed(
                "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private ResponseEntity<TokenResponse> doPost(InputStream is) {
        TokenRequest tokenRequest = super.readRequest(is, TokenRequest.class);

        Tokens tokens = sessionService.updateTokens(tokenRequest.refreshToken);

        TokenResponse response;
        if (tokens != null) {
            response = new TokenResponse(tokens.getRefreshToken(), tokens.getJwt());
        } else {
            throw ApplicationExceptions.notFound("Token not found").get();
        }

        return new ResponseEntity<>(response, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
