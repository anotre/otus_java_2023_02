package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;

import java.io.IOException;
import java.util.List;

public class ClientsApiServlets extends HttpServlet {
    private final DBServiceClient dbServiceClient;
    private final Gson gson;
    private final String URL_SEPARATOR = "/";
    private final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final int ID_PATH_PARAM_POSITION = 1;
    public ClientsApiServlets(DBServiceClient dbServiceClient, Gson gson) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Client> clients = dbServiceClient.findAll();
        resp.setContentType(CONTENT_TYPE);
        ServletOutputStream out = resp.getOutputStream();
        out.print(gson.toJson(clients));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nameParam = req.getParameter("name");
        String addressParam = req.getParameter("address");
        String phoneParam = req.getParameter("phone");
        Address address = new Address(null, addressParam);
        Phone phone = new Phone(null, phoneParam);
        dbServiceClient.saveClient(new Client(null, nameParam, address, List.of(phone)));
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split(URL_SEPARATOR);
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }
}
