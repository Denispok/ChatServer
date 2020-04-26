package app.api;

import app.error.GlobalExceptionHandler;
import app.error.exception.InvalidRequestException;
import app.error.exception.MethodNotAllowedException;
import app.error.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Handler {

    private final ObjectMapper objectMapper;
    private final GlobalExceptionHandler exceptionHandler;

    public Handler(ObjectMapper objectMapper,
                   GlobalExceptionHandler exceptionHandler) {
        this.objectMapper = objectMapper;
        this.exceptionHandler = exceptionHandler;
    }

    public void handle(HttpExchange exchange) {
        try {
            execute(exchange);
        } catch (Exception e) {
            exceptionHandler.handle(e, exchange);
        }
    }

    protected abstract void execute(HttpExchange exchange) throws Exception;

    protected void checkMethod(HttpExchange exchange, HttpMethod method) {
        if (!method.text.equals(exchange.getRequestMethod())) {
            throw new MethodNotAllowedException("Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI());
        }
    }

    protected <T> void sendResponse(HttpExchange exchange, ResponseEntity<T> responseEntity) throws IOException {
        byte[] response;
        exchange.getResponseHeaders().putAll(responseEntity.getHeaders());
        exchange.sendResponseHeaders(responseEntity.getStatusCode().getCode(), 0);
        response = writeResponse(responseEntity.getBody());

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    protected String parseJWT(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null) throw new UnauthorizedException("No Authorization header");
        String[] auth = header.split(" ");
        if (auth.length == 2 && auth[0].equals("Bearer")) {
            return auth[1];
        }
        throw new UnauthorizedException("Wrong Authorization format");
    }

    protected <T> T readRequest(InputStream is, Class<T> type) {
        try {
            return objectMapper.readValue(is, type);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    protected <T> byte[] writeResponse(T response) {
        try {
            if (response == null) return new byte[0];
            return objectMapper.writeValueAsBytes(response);
        } catch (Exception e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    protected static Headers getHeaders(String key, String value) {
        Headers headers = new Headers();
        headers.set(key, value);
        return headers;
    }
}
