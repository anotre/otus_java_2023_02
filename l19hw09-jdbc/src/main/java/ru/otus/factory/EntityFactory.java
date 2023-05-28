package ru.otus.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityFactory<T> {

    private final Class<T> clazz;
    private final Constructor<?> constructor;

    public EntityFactory(Class<T> clazz, Constructor<?> constructor) {
        this.clazz = clazz;
        this.constructor = constructor;
    }

    public T create(Object[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        @SuppressWarnings("unchecked")
        T newInstance = (T) this.constructor.newInstance(args);

        return newInstance;
    }
}
