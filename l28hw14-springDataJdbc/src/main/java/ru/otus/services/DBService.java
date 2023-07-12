package ru.otus.services;

import ru.otus.model.Client;

import java.util.List;
import java.util.Optional;

public interface DBService {
    List<Client> findAll();
    Client save(Client client);
    Optional<Client> getClient(long id);
}
