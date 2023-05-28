package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;

import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final String fieldsString;
    private final String placeholders;
    private final String tableName;
    private final String idFieldName;
    private final int fieldsNumber;

    public <T> EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.fieldsString = getSortedFieldsString(entityClassMetaData.getFieldsWithoutId());
        this.idFieldName = entityClassMetaData.getIdField().getName().toLowerCase();
        this.placeholders = getPlaceholders(entityClassMetaData.getFieldsWithoutId().size());
        this.tableName = entityClassMetaData.getName().toLowerCase();
        this.fieldsNumber = entityClassMetaData.getFieldsWithoutId().size();
        System.out.println();
    }

    @Override
    public String getSelectAllSql() {
        return String.format("select * from %s", tableName);
    }

    @Override
    public String getSelectByIdSql() {
        return String.format(
                "select * from %s where %s = %s",
                tableName,
                idFieldName,
                getPlaceholders(1));
    }

    @Override
    public String getInsertSql() {
        return String.format(
                "insert into %s(%s) values (%s)",
                tableName,
                fieldsString,
                this.placeholders);
    }

    @Override
    public String getUpdateSql() {
        return String.format(
                "update %s set (%s) values (%s) where %s = %s",
                tableName, fieldsString,
                this.placeholders,
                idFieldName,
                getPlaceholders(1));
    }

    private String getSortedFieldsString(List<Field> fields) {
        return fields
                .stream()
                .map(Field::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String getPlaceholders(int size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            stringBuilder.append("?");
            stringBuilder.append(", ");
        }
        stringBuilder.append("?");

        return stringBuilder.toString();
    }
}
