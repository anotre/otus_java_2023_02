package ru.otus.service;

import reactor.core.publisher.Flux;
import ru.otus.domain.Message;

public interface DataStoreDAO extends DataStore {
    Flux<Message> loadMessages();
}
