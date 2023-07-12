package ru.otus.sessionManager;

public interface TransactionClient {
    <T> T doInTransaction(TransactionAction<T> action);
}
