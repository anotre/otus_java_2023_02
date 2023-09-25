package ru.otus.api;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import ru.otus.domain.Message;
import ru.otus.domain.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.service.DataStoreDAO;

import java.util.function.BiFunction;

@RestController
public class DataController {
    private static final Logger log = LoggerFactory.getLogger(DataController.class);
    private final DataStoreDAO dataStore;
    private final Scheduler workerPool;
    private final String magicRoomId = "1408";

    public DataController(@Qualifier("dataStoreExtended") DataStoreDAO dataStore, Scheduler workerPool) {
        this.dataStore = dataStore;
        this.workerPool = workerPool;
    }

    @PostMapping(value = "/msg/{roomId}")
    public Flux<MessageDto> messageFromChat(@PathVariable("roomId") String roomId,
                                      @RequestBody MessageDto messageDto) {
        var messageStr = messageDto.messageStr();
        log.info("messageFromChat, roomId:{}, msg:{} done", roomId, messageStr);

        return Mono.just(new Message(roomId, messageStr))
                .doOnNext(msg -> log.info("messageFromChat:{}", msg))
                .filter(message -> !this.magicRoomId.equals(message.getRoomId()))
                .flatMap(dataStore::saveMessage)
                .publishOn(workerPool)
                .doOnNext(msgSaved -> log.info("msgSaved id:{}", msgSaved.getId()))
                .flatMapMany(message -> {
                    var sourceMessageDto = new MessageDto(message.getId(), message.getMsgText(), message.getRoomId());

                    return Flux.generate(() -> sourceMessageDto, (BiFunction<MessageDto, SynchronousSink<MessageDto>, MessageDto>) (prev, sink) -> {
                        sink.next(prev);
                        if (this.magicRoomId.equals(prev.roomId())) {
                            sink.complete();
                        }

                        return new MessageDto(prev.messageId(), prev.messageStr(), this.magicRoomId);
                    });
                })
                .doOnNext(msgDto -> log.info("messageDTO.id:{}", msgDto.messageId()))
                .subscribeOn(workerPool);
    }

    @GetMapping(value = "/msg/{roomId}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MessageDto> getMessagesByRoomId(@PathVariable("roomId") String roomId) {
        return Mono.just(roomId)
                .doOnNext(room -> log.info("getMessagesByRoomId, room:{}", room))
                .flatMapMany(room -> {
                    if (!this.magicRoomId.equals(room)) {
                        return dataStore.loadMessages(room);
                    }

                    return dataStore.loadMessages();
                })
                .map(message -> new MessageDto(message.getId(), message.getMsgText(), message.getRoomId()))
                .doOnNext(msgDto -> log.info("msgDto:{}", msgDto))
                .subscribeOn(workerPool);
    }
}