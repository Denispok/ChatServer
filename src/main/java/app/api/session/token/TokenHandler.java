package app.api.session.token;

import app.api.*;
import app.error.GlobalExceptionHandler;
import app.error.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import domain.session.SessionService;
import domain.session.Tokens;

import java.io.IOException;
import java.io.InputStream;

public class TokenHandler extends Handler {

    private final SessionService sessionService;

    public TokenHandler(SessionService sessionService, ObjectMapper objectMapper,
                        GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.sessionService = sessionService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        super.checkMethod(exchange, HttpMethod.POST);
        ResponseEntity<TokenResponse> responseEntity = doPost(exchange.getRequestBody());
        super.sendResponse(exchange, responseEntity);
    }

    private ResponseEntity<TokenResponse> doPost(InputStream is) {
        TokenRequest tokenRequest = super.readRequest(is, TokenRequest.class);

        Tokens tokens = sessionService.updateTokens(tokenRequest.refreshToken);

        TokenResponse response;
        if (tokens != null) {
            response = new TokenResponse(tokens.getRefreshToken(), tokens.getJwt());
        } else {
            throw new ResourceNotFoundException("Token not found");
        }

        return new ResponseEntity<>(response, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
