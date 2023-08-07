package ru.otus.service;

import ru.otus.Listener;
import ru.otus.protobuf.generated.RemoteSequenceServiceGrpc;
import ru.otus.protobuf.generated.SequenceElementMessage;
import ru.otus.protobuf.generated.SequenceRangeMessage;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SequenceClientService<T extends SequenceElementMessage> implements Listener<T> {
    private final RemoteSequenceServiceGrpc.RemoteSequenceServiceStub gRPCStub;
    private final CountDownLatch latch;
    private final StreamObserver<SequenceElementMessage> streamObserver;
    private int lastSequenceValue = 0;

    public SequenceClientService(RemoteSequenceServiceGrpc.RemoteSequenceServiceStub gRPCStub, StreamObserver<SequenceElementMessage> streamObserver, CountDownLatch latch) {
        this.streamObserver = streamObserver;
        this.gRPCStub = gRPCStub;
        this.latch = latch;
    }

    @Override
    public void onUpdate(T sequenceElementMessage) {
        this.lastSequenceValue = sequenceElementMessage.getSequenceElement();
    }
    @Override
    public void onComplete() {
        this.latch.countDown();
    }

    public void getSequence(SequenceRangeMessage sequenceRange) {
        this.gRPCStub.getSequence(sequenceRange, this.streamObserver);
    }

    public void printInternalSequence(int from, int to) throws InterruptedException {
        final int COEFFICIENT_OF_VARIATION = 1;
        int currentValue = from;
        for (int i  = from; i <= to; i++) {
            currentValue = currentValue + this.lastSequenceValue + COEFFICIENT_OF_VARIATION;
            this.lastSequenceValue = 0;
            System.out.printf("currentValue: %d\n", currentValue);
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }
}
