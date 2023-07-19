package ru.otus.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ClientResultSetExtractor implements ResultSetExtractor<List<Client>> {

    @Override
    public List<Client> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Client> clientMap = new HashMap<>();

        while (rs.next()) {
            long clientId = rs.getLong("client_id");

            if (!clientMap.containsKey(clientId)) {
                var address = new Address(
                    rs.getLong("address_id"),
                    rs.getString("street"));

                var client = new Client(
                    clientId,
                    rs.getString("client_name"),
                    address,
                    new HashSet<>()
                );

                clientMap.put(clientId, client);
            }

            if (clientMap.containsKey(clientId)) {
                var phone = new Phone(rs.getLong("phone_id"), rs.getString("number"));
                clientMap.get(clientId).getPhones().add(phone);
            }
        }

        return new ArrayList<>(clientMap.values());
    }
}
