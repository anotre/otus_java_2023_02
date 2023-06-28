package ru.otus.dao;

import ru.otus.model.User;

import java.util.*;

public class InMemoryUserDao implements UserDao {

    private final Map<Long, User> users;

    public InMemoryUserDao() {
        users = new HashMap<>();
        users.put(1L, new User(1L, "Саня Админ", "admin", "admin"));
    }

    @Override
    public Optional<User> findById(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findRandomUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(v -> v.getLogin().equals(login)).findFirst();
    }
}
