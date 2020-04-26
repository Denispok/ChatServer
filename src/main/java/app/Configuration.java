package app;

import app.error.GlobalExceptionHandler;
import data.session.InMemorySessionRepository;
import data.user.InMemoryUserRepository;
import domain.session.SessionRepository;
import domain.session.SessionService;
import domain.user.UserRepository;
import domain.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final UserRepository USER_REPOSITORY = new InMemoryUserRepository();
    private static final UserService USER_SERVICE = new UserService(USER_REPOSITORY);

    private static final SessionRepository SESSION_REPOSITORY = new InMemorySessionRepository();
    private static final SessionService SESSION_SERVICE = new SessionService(SESSION_REPOSITORY);

    private static final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }


    static UserRepository getUserRepository() {
        return USER_REPOSITORY;
    }

    public static UserService getUserService() {
        return USER_SERVICE;
    }


    static SessionRepository getSessionRepository() {
        return SESSION_REPOSITORY;
    }

    public static SessionService getSessionService() {
        return SESSION_SERVICE;
    }


    public static GlobalExceptionHandler getErrorHandler() {
        return GLOBAL_ERROR_HANDLER;
    }
}
