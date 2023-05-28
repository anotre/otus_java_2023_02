package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.util.*;

import ru.otus.crm.service.annotation.Id;
import ru.otus.crm.service.annotation.EntityConstructor;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final String tableName;
    private final Map<String, Field> fields = new HashMap<>();
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private String idFieldName;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.tableName = clazz.getSimpleName();
        this.clazz = clazz;
        this.constructor = this.findConstructor(clazz);

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            String fieldName = field.getName();
            if (field.isAnnotationPresent(Id.class)) {
                if (Objects.nonNull(this.idFieldName)) {
                    throw new RuntimeException(String.format(
                            "Cannot be more than one @Id annotated field in %s class.",
                            this.clazz.getCanonicalName()));
                }

                this.idFieldName = fieldName;
            }

            this.fields.put(fieldName, field);
        });

        if (Objects.isNull(this.idFieldName)) {
            throw new RuntimeException(String.format(
                    "No @Id annotated field found in %s class.",
                    this.clazz.getCanonicalName()));
        }

    }

    @Override
    public String getName() {
        return this.tableName;
    }

    @Override
    public Constructor<T> getConstructor() {
        return this.constructor;
    }

    @Override
    public Field getIdField() {
        return this.fields.get(this.idFieldName);
    }

    @Override
    public List<Field> getAllFields() {
        return List.copyOf(this.fields.values());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        Map<String, Field> mapFieldsWithoutId = new HashMap<>(this.fields);
        mapFieldsWithoutId.remove(this.idFieldName);

        return List.copyOf(mapFieldsWithoutId.values());
    }

    @SuppressWarnings("unchecked")
    private Constructor<T> findConstructor(Class<T> entityClass) {

        Constructor<T> foundConstructor = null;

        for (Constructor<?> constructor : entityClass.getConstructors()) {
            if (constructor.isAnnotationPresent(EntityConstructor.class)) {
                if (Objects.nonNull(foundConstructor)) {
                    throw new RuntimeException(
                            String.format("Cannot be more than one annotated constructor in %s class", this.tableName));
                }

                foundConstructor = (Constructor<T>) constructor;
            }
        }

        if (Objects.isNull(foundConstructor)) {
            throw new RuntimeException(
                    String.format("A class %s must have one constructor explicitly annotated", this.tableName));
        }

        return foundConstructor;
    }
}
