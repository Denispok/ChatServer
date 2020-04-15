package app;

import app.api.session.login.LoginHandler;
import app.api.session.token.TokenHandler;
import app.api.user.registration.RegistrationHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static app.Configuration.*;

class Application {

    public static void main(String[] args) throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        RegistrationHandler registrationHandler = new RegistrationHandler(getUserService(), getObjectMapper(), getErrorHandler());
        server.createContext("/api/user/registration/signup", registrationHandler::handle);

        LoginHandler loginHandler = new LoginHandler(getSessionService(), getObjectMapper(), getErrorHandler());
        server.createContext("/api/auth/login", loginHandler::handle);

        TokenHandler tokenHandler = new TokenHandler(getSessionService(), getObjectMapper(), getErrorHandler());
        server.createContext("/api/auth/token", tokenHandler::handle);

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
