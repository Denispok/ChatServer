package app.api.user.edit;

import app.api.*;
import app.errors.ApplicationExceptions;
import app.errors.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import domain.user.UserService;
import domain.user.model.EditUser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditHandler extends Handler {

    private final UserService userService;

    public EditHandler(UserService userService, ObjectMapper objectMapper, GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.userService = userService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        byte[] response;
        if ("PUT".equals(exchange.getRequestMethod())) {
            var jwt = parseJWT(exchange.getRequestHeaders().getFirst("Authorization"));
            if (jwt == null) throw ApplicationExceptions.unauthorized("Unauthorized").get();
            ResponseEntity<EmptyResponse> responseEntity = doPost(exchange.getRequestBody(), jwt);
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

    private String parseJWT(String header) {
        if (header == null) return null;
        String[] auth = header.split(" ");
        if (auth.length == 2 && auth[0].equals("Bearer")) {
            return auth[1];
        }
        return null;
    }

    private ResponseEntity<EmptyResponse> doPost(InputStream is, String jwt) {
        EditRequest request = super.readRequest(is, EditRequest.class);

        EditUser user = EditUser.builder()
            .login(request.getLogin())
            .password(request.getPassword())
            .build();

        String userId = userService.edit(jwt, user);
        if (userId == null) throw ApplicationExceptions.notFound("Internal error").get();

        return new ResponseEntity<>(null, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
