package ru.otus.repository;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import ru.otus.domain.Message;

public interface MessageRepositorySortable extends MessageRepository, ReactiveSortingRepository<Message, Long> {

}
