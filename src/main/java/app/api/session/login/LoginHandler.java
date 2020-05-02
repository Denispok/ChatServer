package app.api.session.login;

import app.api.*;
import app.error.GlobalExceptionHandler;
import app.error.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import domain.session.SessionService;
import domain.session.model.Tokens;
import domain.user.model.UserLoginInfo;

import java.io.IOException;
import java.io.InputStream;

public class LoginHandler extends Handler {

    private final SessionService sessionService;

    public LoginHandler(SessionService sessionService, ObjectMapper objectMapper,
                        GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.sessionService = sessionService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        super.checkMethod(exchange, HttpMethod.POST);
        ResponseEntity<LoginResponse> responseEntity = doPost(exchange.getRequestBody());
        super.sendResponse(exchange, responseEntity);
    }

    private ResponseEntity<LoginResponse> doPost(InputStream is) {
        LoginRequest loginRequest = super.readRequest(is, LoginRequest.class);

        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
            .login(loginRequest.login)
            .password(loginRequest.password)
            .build();

        Tokens tokens = sessionService.signIn(userLoginInfo);

        LoginResponse response;
        if (tokens != null) {
            response = new LoginResponse(tokens.getRefreshToken(), tokens.getJwt());
        } else {
            throw new ResourceNotFoundException("User not found");
        }

        return new ResponseEntity<>(response, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
