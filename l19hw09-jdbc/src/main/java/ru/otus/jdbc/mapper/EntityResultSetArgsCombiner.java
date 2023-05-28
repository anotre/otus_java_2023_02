package ru.otus.jdbc.mapper;

import ru.otus.crm.service.annotation.ConstructorParam;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntityResultSetArgsCombiner<T> {

  private final Constructor<T> constructor;
  private final EntityClassMetaData<T> entityClassMetaData;
  private final List<String> sortedFieldNames;

  public EntityResultSetArgsCombiner(EntityClassMetaData<T> entityClassMetaData) {
    this.entityClassMetaData = entityClassMetaData;
    this.constructor = this.entityClassMetaData.getConstructor();
    this.sortedFieldNames = this.getSortedFieldNames(this.constructor);
  }

  public Object[] combine(ResultSet rs) {

    List<Object> args = new ArrayList<>();

    sortedFieldNames.forEach(fieldName -> {
      try {
        args.add(rs.getObject(fieldName));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });

    if (args.size() != sortedFieldNames.size()) {
      throw new RuntimeException(
              "The number of arguments to call a constructor does not match the number of its parameters.");
    }

    return args.toArray();
  }

  private List<String> getSortedFieldNames(Constructor<T> constructor) {
    Parameter[] params = constructor.getParameters();
    List<String> sortedFieldNames = new ArrayList<>();

    for (int i = 0; i < params.length; i++) {
      if (!params[i].isAnnotationPresent(ConstructorParam.class)) {
        throw new RuntimeException(
                String.format(
                        "Every constructor parameter of %s class must to have the field annotation",
                        entityClassMetaData.getName()));
      };

      String fieldName = params[i].getAnnotation(ConstructorParam.class).fieldName();

      if (fieldName.trim().isEmpty()) {
        throw new RuntimeException(
                String.format(
                        "Constructor parameter in %s class cannot be empty",
                        entityClassMetaData.getName()));
      }

      sortedFieldNames.add(fieldName);
    }

    return List.copyOf(sortedFieldNames);
  }
}
