package app.api.user.edit;

import app.api.*;
import app.error.GlobalExceptionHandler;
import app.error.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import domain.user.UserService;
import domain.user.model.EditUser;

import java.io.IOException;
import java.io.InputStream;

public class EditHandler extends Handler {

    private final UserService userService;

    public EditHandler(UserService userService, ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userService = userService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        super.checkMethod(exchange, HttpMethod.PUT);
        String jwt = parseJWT(exchange);
        ResponseEntity<EmptyResponse> responseEntity = doPut(exchange.getRequestBody(), jwt);
        super.sendResponse(exchange, responseEntity);
    }

    private ResponseEntity<EmptyResponse> doPut(InputStream is, String jwt) {
        EditRequest request = super.readRequest(is, EditRequest.class);

        EditUser user = EditUser.builder()
            .login(request.getLogin())
            .password(request.getPassword())
            .build();

        String userId = userService.edit(jwt, user);
        if (userId == null) throw new ResourceNotFoundException("Internal error");

        return new ResponseEntity<>(null, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
