package app.api.user.registration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import app.api.Constants;
import app.api.Handler;
import app.api.ResponseEntity;
import app.api.StatusCode;
import app.errors.ApplicationExceptions;
import app.errors.GlobalExceptionHandler;
import domain.user.model.NewUser;
import domain.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

public class RegistrationHandler extends Handler {

    private final UserService userService;

    public RegistrationHandler(UserService userService, ObjectMapper objectMapper,
                               GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userService = userService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        byte[] response;
        if ("POST".equals(exchange.getRequestMethod())) {
            ResponseEntity<RegistrationResponse> responseEntity = doPost(exchange.getRequestBody());
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

    private ResponseEntity<RegistrationResponse> doPost(InputStream is) {
        RegistrationRequest registerRequest = super.readRequest(is, RegistrationRequest.class);

        NewUser user = NewUser.builder()
            .login(registerRequest.getLogin())
            .password(registerRequest.getPassword())
            .build();

        String userId = userService.create(user);

        RegistrationResponse response = new RegistrationResponse(userId);

        return new ResponseEntity<>(response, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
