package ru.otus.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.model.Client;
import ru.otus.repository.ClientRepository;
import ru.otus.sessionManager.TransactionClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DBServiceClient implements DBService {
    private final TransactionClient transactionClient;
    private final ClientRepository clientRepository;
    
    @Override
    public List<Client> findAll() {
        return new ArrayList<Client>(clientRepository.findAll());
    }

    @Override
    public Client save(Client client) {
        return transactionClient.doInTransaction(() -> clientRepository.save(client));
    }

    @Override
    public Optional<Client> getClient(long id) {
        return clientRepository.findById(id);
    }
}
