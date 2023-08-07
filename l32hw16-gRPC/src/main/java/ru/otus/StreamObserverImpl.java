package ru.otus;

import ru.otus.protobuf.generated.SequenceElementMessage;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;

public class StreamObserverImpl<T extends SequenceElementMessage> implements StreamObserver<T> {
    private final List<Listener<T>> listeners;
    public StreamObserverImpl() {
        this.listeners = new ArrayList<>();
    }
    @Override
    public void onNext(T value) {
        listeners.forEach(listener -> listener.onUpdate(value));
    }

    @Override
    public void onError(Throwable t) {
        throw new RuntimeException(t.getMessage());
    }

    @Override
    public void onCompleted() {
        listeners.forEach(Listener::onComplete);
    }

    void addListener(Listener<T> listener) {
        this.listeners.add(listener);
    }

    void removeListener(Listener<T> listener) {
        this.listeners.remove(listener);
    }
}