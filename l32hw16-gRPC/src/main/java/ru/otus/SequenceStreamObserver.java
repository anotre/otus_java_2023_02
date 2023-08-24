package ru.otus;

import ru.otus.protobuf.generated.SequenceElementMessageOrBuilder;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;

public class SequenceStreamObserver<T extends SequenceElementMessageOrBuilder> implements StreamObserver<T> {
    private final CountDownLatch latch;
    private T lastSequenceElementMessage;
    public SequenceStreamObserver(CountDownLatch latch) {
        this.latch = latch;
        this.setLastSequenceElementMessage(null);
    }

    @Override
    public void onNext(T value) {
            this.setLastSequenceElementMessage(value);
    }

    @Override
    public void onError(Throwable t) {
        throw new RuntimeException(t.getMessage());
    }

    @Override
    public void onCompleted() {
        this.latch.countDown();
    }

    public synchronized T getLastSequenceElementMessageAndReset() {
        var buffer = this.lastSequenceElementMessage;
        this.setLastSequenceElementMessage(null);

        return buffer;
    }

    private synchronized void setLastSequenceElementMessage(T lastSequenceElement) {
        this.lastSequenceElementMessage = lastSequenceElement;
    }
}
