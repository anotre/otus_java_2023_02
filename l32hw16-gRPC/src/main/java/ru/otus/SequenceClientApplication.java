package ru.otus;

import ru.otus.protobuf.generated.RemoteSequenceServiceGrpc;
import ru.otus.protobuf.generated.SequenceElementMessage;
import ru.otus.protobuf.generated.SequenceRangeMessage;
import ru.otus.service.SequenceClientService;

import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class SequenceClientApplication {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final int SEQUENCE_FROM = 0;
    private static final int SEQUENCE_TO = 30;
    private static final int INNER_SEQUENCE_FROM = 0;
    private static final int INNER_SEQUENCE_TO = 50;
    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        var stub = RemoteSequenceServiceGrpc.newStub(channel);
        var latch = new CountDownLatch(1);
        var sequenceElementStoreMessageStorage = new ArrayBlockingQueue<SequenceElementMessage>(1);
        var sequenceStreamObserver = new SequenceStreamObserver<SequenceElementMessage>(sequenceElementStoreMessageStorage, latch);
        var sequenceClientService = new SequenceClientService<SequenceElementMessage>(stub, sequenceStreamObserver, sequenceElementStoreMessageStorage);

        var sequenceRange = SequenceRangeMessage
                .newBuilder()
                .setFrom(SEQUENCE_FROM)
                .setTo(SEQUENCE_TO)
                .build();
        sequenceClientService.getSequence(sequenceRange);
        sequenceClientService.printInternalSequence(INNER_SEQUENCE_FROM, INNER_SEQUENCE_TO);

        latch.await();
        channel.shutdown();
    }
}