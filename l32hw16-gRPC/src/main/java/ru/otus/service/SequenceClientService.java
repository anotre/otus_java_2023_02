package ru.otus.service;

import ru.otus.SequenceStreamObserver;
import ru.otus.protobuf.generated.RemoteSequenceServiceGrpc;
import ru.otus.protobuf.generated.SequenceElementMessage;
import ru.otus.protobuf.generated.SequenceRangeMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceClientService {
    private final RemoteSequenceServiceGrpc.RemoteSequenceServiceStub gRPCStub;
    private final SequenceStreamObserver<SequenceElementMessage> streamObserver;

    public SequenceClientService(RemoteSequenceServiceGrpc.RemoteSequenceServiceStub gRPCStub,
                                 SequenceStreamObserver<SequenceElementMessage> streamObserver) {
        this.streamObserver = streamObserver;
        this.gRPCStub = gRPCStub;
    }

    public void getSequence(SequenceRangeMessage sequenceRange) {
        this.gRPCStub.getSequence(sequenceRange, this.streamObserver);
    }

    public void printInternalSequence(int from, int to) {
        final int COEFFICIENT_OF_VARIATION = 1;
        var executor = Executors.newScheduledThreadPool(1);
        AtomicInteger currentIteration = new AtomicInteger(from);
        AtomicInteger currentValue = new AtomicInteger(from);

        Runnable task = () -> {
                currentValue.set(currentValue.get() + this.getNextValue() + COEFFICIENT_OF_VARIATION);
                System.out.printf("currentValue: %d\n", currentValue.get());

                if (currentIteration.incrementAndGet() > to) {
                    executor.shutdown();
                }
        };

        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private int getNextValue() {
        int nextValue = 0;
        var lastValue = this.streamObserver.getLastSequenceElementMessageAndReset();

        if (lastValue != null) {
            nextValue = lastValue.getSequenceElement();
        }

        return nextValue;
    }
}
