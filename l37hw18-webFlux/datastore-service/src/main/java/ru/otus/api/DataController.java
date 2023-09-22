package ru.otus.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.otus.domain.Message;
import ru.otus.domain.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.service.DataStoreDAO;

@RestController
public class DataController {
    private static final Logger log = LoggerFactory.getLogger(DataController.class);
    private final DataStoreDAO dataStore;
    private final Scheduler workerPool;
    private final long magicRoomId = 1408;

    public DataController(@Qualifier("dataStoreExtended") DataStoreDAO dataStore, Scheduler workerPool) {
        this.dataStore = dataStore;
        this.workerPool = workerPool;
    }

    @PostMapping(value = "/msg/{roomId}")
    public Mono<Long> messageFromChat(@PathVariable("roomId") String roomId,
                                      @RequestBody MessageDto messageDto) {
        var messageStr = messageDto.messageStr();

        var msgId = Mono.just(new Message(roomId, messageStr))
                .doOnNext(msg -> log.info("messageFromChat:{}", msg))
                .filter(message -> Long.parseLong(message.getRoomId()) != this.magicRoomId)
                .flatMap(dataStore::saveMessage)
                .publishOn(workerPool)
                .doOnNext(msgSaved -> log.info("msgSaved id:{}", msgSaved.getId()))
                .map(Message::getId)
                .subscribeOn(workerPool);

        log.info("messageFromChat, roomId:{}, msg:{} done", roomId, messageStr);
        return msgId;
    }

    @GetMapping(value = "/msg/{roomId}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MessageDto> getMessagesByRoomId(@PathVariable("roomId") String roomId) {
        return Mono.just(roomId)
                .doOnNext(room -> log.info("getMessagesByRoomId, room:{}", room))
                .flatMapMany(room -> {
                    if (Long.parseLong(room) != this.magicRoomId) {
                        return dataStore.loadMessages(room);
                    }

                    return dataStore.loadMessages();
                })
                .map(message -> new MessageDto(message.getMsgText()))
                .doOnNext(msgDto -> log.info("msgDto:{}", msgDto))
                .subscribeOn(workerPool);
    }
}
/**
 * если не 1408 - только из комнаты
 * если 1408 - все
 * почему roomId 1408 чужие обновления после загрузки
 */