package ru.otus;

import ru.otus.service.RemoteSequenceServiceImpl;

import io.grpc.ServerBuilder;
import java.io.IOException;

public class SequenceServerApplication {
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        var sequenceService = new RemoteSequenceServiceImpl();
        var server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(sequenceService)
                .build();
        server.start();
        server.awaitTermination();
    }
}