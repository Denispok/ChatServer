package app.api.session.login;

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
import domain.user.UserLoginInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginHandler extends Handler {

    private final SessionService sessionService;

    public LoginHandler(SessionService sessionService, ObjectMapper objectMapper,
                        GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.sessionService = sessionService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        byte[] response;
        if ("POST".equals(exchange.getRequestMethod())) {
            ResponseEntity<LoginResponse> responseEntity = doPost(exchange.getRequestBody());
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
            throw ApplicationExceptions.notFound("User not found").get();
        }

        return new ResponseEntity<>(response, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
