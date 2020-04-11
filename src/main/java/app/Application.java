package app;

import static app.Configuration.getErrorHandler;
import static app.Configuration.getObjectMapper;
import static app.Configuration.getUserService;
import static app.api.ApiUtils.splitQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import app.api.user.RegistrationHandler;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

class Application {

    public static void main(String[] args) throws IOException {
        int serverPort = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        RegistrationHandler registrationHandler = new RegistrationHandler(getUserService(), getObjectMapper(), getErrorHandler());
        server.createContext("/api/user/registration/signup", registrationHandler::handle);

        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
