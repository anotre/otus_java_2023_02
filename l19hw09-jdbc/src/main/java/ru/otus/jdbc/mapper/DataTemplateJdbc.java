package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.factory.EntityFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;

    private final EntityFactory<T> entityFactory;
    EntityResultSetArgsCombiner<T> argsCombiner;
    EntityClassDataProvider<T> dataProvider;

    public DataTemplateJdbc(DbExecutor dbExecutor,
                            EntitySQLMetaData entitySQLMetaData,
                            EntityFactory<T> entityFactory,
                            EntityResultSetArgsCombiner<T> argsCombiner,
                            EntityClassDataProvider<T> dataProvider) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityFactory = entityFactory;
        this.argsCombiner = argsCombiner;
        this.dataProvider = dataProvider;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return entityFactory.create(argsCombiner.combine(rs));
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
                            responseEntities.add(entityFactory.create(argsCombiner.combine(rs)));
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
                    dataProvider.getData(entity));
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
                    dataProvider.getDataWithId(entity));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }
}
