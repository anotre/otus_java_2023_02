package ru.otus;

import ru.otus.protobuf.generated.SequenceElementMessage;

public interface Listener<T extends SequenceElementMessage> {
    void onUpdate(T value);
    void onComplete();
}
