package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.crm.service.annotation.Accessor;
import ru.otus.crm.service.annotation.AccessorType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private final DbExecutor dbExecutor;
    private final EntityClassMetaData<T> entityClassMetaData;
    private final EntitySQLMetaData entitySQLMetaData; // по-идее должен содержать только метаданные запроса
    private final Map<String, Method> fieldGetters;
    private final Map<String, Method> fieldSetters;
    private final String idFieldName;
    private final Class<T> clazz;

    public DataTemplateJdbc(DbExecutor dbExecutor,
                            EntityClassMetaData<T> entityClassMetaData,
                            EntitySQLMetaData entitySQLMetaData,
                            Class<T> clazz) {
        this.clazz = clazz;
        this.dbExecutor = dbExecutor;
        this.entityClassMetaData = entityClassMetaData;
        this.entitySQLMetaData = entitySQLMetaData;
        this.idFieldName = entityClassMetaData.getIdField().getName();
        Method[] allMethods = this.clazz.getDeclaredMethods();
        this.fieldGetters = this.getAccessMethods(AccessorType.GETTER, allMethods);
        this.fieldSetters = this.getAccessMethods(AccessorType.SETTER, allMethods);
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createEntity(getResultSetEntityData(rs, this.fieldSetters.keySet()));
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(
                connection,
                entitySQLMetaData.getSelectAllSql(),
                Collections.emptyList(),
                rs -> {
                    List<T> responseEntities = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            responseEntities.add(createEntity(getResultSetEntityData(rs, this.fieldSetters.keySet())));
                        }

                        return Collections.unmodifiableList(responseEntities);
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        try {
            return dbExecutor.executeStatement(
                    connection, 
                    entitySQLMetaData.getInsertSql(),
                    getRequestArgs(entity));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            dbExecutor.executeStatement(
                    connection,
                    entitySQLMetaData.getUpdateSql(),
                    getRequestArgsWithId(entity));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T createEntity(Map<String, Object> args) throws
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        var newEntity = this.entityClassMetaData.getConstructor().newInstance();
        this.fieldSetters.forEach((field, setter) -> {
            try {
                setter.invoke(newEntity, args.get(field));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        return newEntity;
    }

    private Map<String, Object> getResultSetEntityData(ResultSet rs, Set<String> fieldNames) {
        Map<String, Object> entityData = new HashMap<>();
        fieldNames.forEach(fieldName -> {
            try {
                entityData.put(fieldName, rs.getObject(fieldName));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return entityData;
    }

    private List<Object> getRequestArgs(T entity) {
        List<Object> fieldsData = new ArrayList<>();

        this.fieldGetters.forEach((name, method) -> {
            if (name.equals(this.idFieldName)) {
                return;
            }
            
            fieldsData.add(this.invokeMethod(method, entity));
        });

        return fieldsData;
    }

    private List<Object> getRequestArgsWithId(T entity) {
        List<Object> fieldsData = new ArrayList<>(this.getRequestArgs(entity));

        for (Map.Entry<String, Method> entry : this.fieldGetters.entrySet()) {
            String name = entry.getKey();
            Method method = entry.getValue();
            fieldsData.add(this.invokeMethod(method, entity));
        }

        return fieldsData;
    }

    private Object invokeMethod(Method method, T entity) {
        try {
            return method.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Method> getAccessMethods(AccessorType type, Method[] methods) {
        var accessorClass = Accessor.class;
        Map<String, Method> accessors = new HashMap<>();

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Accessor.class)) {
                var annotation = methods[i].getAnnotation(accessorClass);
                String fieldName = annotation.fieldName();

                if (annotation.type().equals(type)) {
                    if (accessors.containsKey(fieldName)) {
                        throw new RuntimeException(String.format("Annotation placement error in %s class", this.clazz.getSimpleName()));
                    }

                    accessors.put(fieldName, methods[i]);
                }
            }
        }

        return accessors;
    }
}
