package ru.otus.server;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.helpers.FileSystemHelper;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;
import ru.otus.servlet.AuthorizationFilter;
import ru.otus.servlet.ClientsApiServlets;
import ru.otus.servlet.ClientsServlet;
import ru.otus.servlet.LoginServlet;

import java.util.Arrays;

public class ClientWebServerWithFilterBasedSecurity implements ClientWebServer {
    private static final String START_PAGE_NAME = "clients.html";
    private static final String COMMON_RESOURCES_DIR = "static";
    private final DBServiceClient dbServiceClient;
    private final UserAuthService authService;
    private final Gson gson;
    protected final TemplateProcessor templateProcessor;
    private final Server server;

    public ClientWebServerWithFilterBasedSecurity(
            int port,
            DBServiceClient dbServiceClient,
            UserAuthService authService,
            Gson gson,
            TemplateProcessor templateProcessor) {
        this.dbServiceClient = dbServiceClient;
        this.authService = authService;
        this.gson = gson;
        this.templateProcessor = templateProcessor;
        this.server = new Server(port);
    }
    @Override
    public void start() throws Exception {
        if (server.getHandlers().length == 0) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        this.server.join();
    }

    @Override
    public void stop() throws Exception {
        this.server.stop();
    }

    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(
                new ServletHolder(
                        new LoginServlet(
                                this.templateProcessor,
                                this.authService)),
                "/login");
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths)
                .forEachOrdered(
                        path -> servletContextHandler.addFilter(
                                new FilterHolder(authorizationFilter),
                                path,
                                null));

        return servletContextHandler;
    }

    private Server initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        HandlerList handlers = new HandlerList();
        handlers.addHandler(resourceHandler);
        handlers.addHandler(applySecurity(servletContextHandler, "/clients", "api/clients/*"));
        server.setHandler(handlers);

        return server;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setWelcomeFiles(new String[]{START_PAGE_NAME});
        resourceHandler.setResourceBase(FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));

        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        var servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(new ServletHolder(new ClientsServlet(templateProcessor, dbServiceClient)), "/clients");
        servletContextHandler.addServlet(new ServletHolder(new ClientsApiServlets(dbServiceClient, gson)), "/api/clients/*");

        return servletContextHandler;
    }


}
