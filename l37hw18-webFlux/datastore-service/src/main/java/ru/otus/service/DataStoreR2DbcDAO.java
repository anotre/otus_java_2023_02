package ru.otus.service;

import org.springframework.data.domain.Sort;
import ru.otus.domain.Message;
import ru.otus.repository.MessageRepositorySortable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import java.time.Duration;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service("dataStoreExtended")
public class DataStoreR2DbcDAO extends DataStoreR2dbc implements DataStoreDAO {
    private static final Logger log = LoggerFactory.getLogger(DataStoreR2DbcDAO.class);
    private final MessageRepositorySortable messageRepository;
    private final Scheduler workerPool;

    public DataStoreR2DbcDAO(Scheduler workerPool, MessageRepositorySortable messageRepository) {
        super(workerPool, messageRepository);
        this.workerPool = workerPool;
        this.messageRepository = messageRepository;
    }

    public Flux<Message> loadMessages() {
        log.info("loadMessages from mystery room 1408:");

        return messageRepository
                .findAll(Sort.by(Sort.Direction.ASC, "id"))
                .delayElements(Duration.of(3, SECONDS), workerPool);
    }
}
