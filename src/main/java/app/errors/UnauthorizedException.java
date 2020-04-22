package app.errors;

public class UnauthorizedException extends ApplicationException {

    UnauthorizedException(int code, String message) {
        super(code, message);
    }
}
