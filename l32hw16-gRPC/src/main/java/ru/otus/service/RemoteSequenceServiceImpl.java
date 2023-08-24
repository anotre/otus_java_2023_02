package ru.otus.service;

import ru.otus.protobuf.generated.RemoteSequenceServiceGrpc;
import ru.otus.protobuf.generated.SequenceElementMessage;
import ru.otus.protobuf.generated.SequenceRangeMessage;

import io.grpc.stub.StreamObserver;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteSequenceServiceImpl extends RemoteSequenceServiceGrpc.RemoteSequenceServiceImplBase {
    @Override
    public void getSequence(SequenceRangeMessage request, StreamObserver<SequenceElementMessage> responseObserver) {
        final int from = request.getFrom();
        final int to = request.getTo();
        var currentIteration = new AtomicInteger(from);
        final var executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            responseObserver.onNext(
                    this.getSequenceElementMessage(currentIteration.getAndIncrement())
            );
            if (currentIteration.get() > to) {
                responseObserver.onCompleted();
                executor.shutdown();
            }
        };

        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }

    private SequenceElementMessage getSequenceElementMessage(int value) {
        return SequenceElementMessage
                .newBuilder()
                .setSequenceElement(value)
                .build();
    }
}
