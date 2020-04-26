package app.api.user.registration;

import app.api.*;
import app.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import domain.user.UserService;
import domain.user.model.NewUser;

import java.io.IOException;
import java.io.InputStream;

public class RegistrationHandler extends Handler {

    private final UserService userService;

    public RegistrationHandler(UserService userService, ObjectMapper objectMapper,
                               GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userService = userService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        super.checkMethod(exchange, HttpMethod.POST);
        ResponseEntity<RegistrationResponse> responseEntity = doPost(exchange.getRequestBody());
        super.sendResponse(exchange, responseEntity);
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
