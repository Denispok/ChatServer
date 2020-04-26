package app.error;

import app.api.Constants;
import app.api.ErrorResponse;
import app.api.ErrorResponse.ErrorResponseBuilder;
import app.api.StatusCode;
import app.error.exception.InvalidRequestException;
import app.error.exception.MethodNotAllowedException;
import app.error.exception.ResourceNotFoundException;
import app.error.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(Throwable throwable, HttpExchange exchange) {
        try {
            throwable.printStackTrace();
            exchange.getResponseHeaders().set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            ErrorResponse response = getErrorResponse(throwable, exchange);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(response));
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ErrorResponse getErrorResponse(Throwable throwable, HttpExchange exchange) throws IOException {
        ErrorResponseBuilder responseBuilder = ErrorResponse.builder();
        if (throwable instanceof InvalidRequestException) {
            InvalidRequestException exc = (InvalidRequestException) throwable;
            responseBuilder.code(StatusCode.BAD_REQUEST.getCode()).message(exc.getMessage());
            exchange.sendResponseHeaders(StatusCode.BAD_REQUEST.getCode(), 0);
        } else if (throwable instanceof UnauthorizedException) {
            UnauthorizedException exc = (UnauthorizedException) throwable;
            responseBuilder.code(StatusCode.UNAUTHORIZED.getCode()).message(exc.getMessage());
            exchange.sendResponseHeaders(StatusCode.UNAUTHORIZED.getCode(), 0);
        } else if (throwable instanceof ResourceNotFoundException) {
            ResourceNotFoundException exc = (ResourceNotFoundException) throwable;
            responseBuilder.code(StatusCode.NOT_FOUND.getCode()).message(exc.getMessage());
            exchange.sendResponseHeaders(StatusCode.NOT_FOUND.getCode(), 0);
        } else if (throwable instanceof MethodNotAllowedException) {
            MethodNotAllowedException exc = (MethodNotAllowedException) throwable;
            responseBuilder.code(StatusCode.METHOD_NOT_ALLOWED.getCode()).message(exc.getMessage());
            exchange.sendResponseHeaders(StatusCode.METHOD_NOT_ALLOWED.getCode(), 0);
        } else {
            responseBuilder.code(StatusCode.INTERNAL_SERVER_ERROR.getCode()).message(throwable.getMessage());
            exchange.sendResponseHeaders(StatusCode.INTERNAL_SERVER_ERROR.getCode(), 0);
        }
        return responseBuilder.build();
    }
}
