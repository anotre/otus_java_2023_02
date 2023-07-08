package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientsServlet extends HttpServlet {
    private static final String CLIENTS_PAGE_TEMPLATE = "clients.html";
    private static final String CLIENTS_LIST = "clientsList";

    private final DBServiceClient dbServiceClient;

    private final TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        List<Client> clientsList = dbServiceClient.findAll();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put(CLIENTS_LIST, clientsList);
        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, paramsMap));
    }
}
