package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.util.*;

import ru.otus.crm.service.annotation.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final String entityName;
    private final List<Field> fields = new ArrayList<>();
    private final List<Field> fieldsWithoutId = new ArrayList<>();
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private Field idField;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.entityName = clazz.getSimpleName();
        this.clazz = clazz;

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(Id.class)) {
                if (Objects.nonNull(this.idField)) {
                    throw new RuntimeException(String.format(
                            "Cannot be more than one @Id annotated field in %s class.",
                            this.clazz.getCanonicalName()));
                }

                fields.add(field);
                this.idField = field;
            } else {
                this.fieldsWithoutId.add(field);
                this.fields.add(field);
            }

        });

        if (Objects.isNull(this.idField)) {
            throw new RuntimeException(String.format(
                    "No @Id annotated field found in %s class.",
                    this.clazz.getCanonicalName()));
        }

        try {
            this.constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return this.entityName;
    }

    @Override
    public Constructor<T> getConstructor() {
        return this.constructor;
    }

    @Override
    public Field getIdField() {
        return this.idField;
    }

    @Override
    public List<Field> getAllFields() {
        return List.copyOf(this.fields);
    }

    @Override
    public List<Field> getFieldsWithoutId() {
       return List.copyOf(this.fieldsWithoutId);
    }
}
