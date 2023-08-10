package ru.otus.service;

import ru.otus.model.SequenceElement;
import ru.otus.protobuf.generated.RemoteSequenceServiceGrpc;
import ru.otus.protobuf.generated.SequenceElementMessage;
import ru.otus.protobuf.generated.SequenceElementMessageOrBuilder;
import ru.otus.protobuf.generated.SequenceRangeMessage;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.TimeUnit;

public class RemoteSequenceServiceImpl extends RemoteSequenceServiceGrpc.RemoteSequenceServiceImplBase {
    @Override
    public void getSequence(SequenceRangeMessage request, StreamObserver<SequenceElementMessage> responseObserver) {
        int from = request.getFrom();
        int to = request.getTo();

        for (int i = from; i <= to; i++) {
            try {
                var response = new SequenceElement(i);
                responseObserver.onNext(this.transformToMessage(response));
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        responseObserver.onCompleted();
    }

    private SequenceElementMessage transformToMessage(SequenceElement sequenceElement) {
        return SequenceElementMessage
                .newBuilder()
                .setSequenceElement(sequenceElement.getValue()).build();
    }
}
