package ru.otus.service;

import ru.otus.model.SequenceElement;
import ru.otus.protobuf.generated.RemoteSequenceServiceGrpc;
import ru.otus.protobuf.generated.SequenceElementMessage;
import ru.otus.protobuf.generated.SequenceElementMessageOrBuilder;
import ru.otus.protobuf.generated.SequenceRangeMessage;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SequenceClientService<T extends SequenceElementMessageOrBuilder> {
    private final RemoteSequenceServiceGrpc.RemoteSequenceServiceStub gRPCStub;
    private final StreamObserver<SequenceElementMessage> streamObserver;
    private final ArrayBlockingQueue<SequenceElementMessage> sequenceElementMessagesStorage;

    public SequenceClientService(RemoteSequenceServiceGrpc.RemoteSequenceServiceStub gRPCStub, StreamObserver<SequenceElementMessage> streamObserver, ArrayBlockingQueue<SequenceElementMessage> sequenceElementMessagesStorage) {
        this.streamObserver = streamObserver;
        this.gRPCStub = gRPCStub;
        this.sequenceElementMessagesStorage = sequenceElementMessagesStorage;
    }

    public void getSequence(SequenceRangeMessage sequenceRange) {
        this.gRPCStub.getSequence(sequenceRange, this.streamObserver);
    }

    public void printInternalSequence(int from, int to) throws InterruptedException {
        final int COEFFICIENT_OF_VARIATION = 1;
        int currentValue = from;

        for (int i  = from; i <= to; i++) {
            int lastSequenceElementValue = 0;
            var sequenceElementMessage = sequenceElementMessagesStorage.poll();

            if (sequenceElementMessage != null) {
                lastSequenceElementValue = this.getSequenceElement(sequenceElementMessage).getValue();
            }

            currentValue = currentValue + lastSequenceElementValue + COEFFICIENT_OF_VARIATION;
            System.out.printf("currentValue: %d\n", currentValue);
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }

    public SequenceElement getSequenceElement(SequenceElementMessage sequenceElementMessage) {
        return new SequenceElement(sequenceElementMessage.getSequenceElement());
    }
}
