package ru.otus.jdbc.mapper;

import ru.otus.crm.service.annotation.FieldGetter;

import java.lang.reflect.Method;
import java.util.*;

public class EntityClassDataProvider<T> {
  private final Class<T> clazz;
  private final Map<String, Method> fieldGetters;

  private Method idFieldGetter;

  public EntityClassDataProvider(Class<T> clazz) {
    this.clazz = clazz;
    this.fieldGetters = new TreeMap<>();

    Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
      if (method.isAnnotationPresent(FieldGetter.class)) {
        String fieldName = method.getAnnotation(FieldGetter.class).fieldName();
        int isIdField = method.getAnnotation(FieldGetter.class).isIdField();

        if (isIdField > 0) {
          this.idFieldGetter = method;
          return;
        }

        if (fieldName.trim().isEmpty()) {
          throw new RuntimeException(String.format(
                  "The empty string passed by the annotation in %s class",
                  clazz.getSimpleName()));
        }

        fieldGetters.put(fieldName, method);
      }
    });
  }

  public List<Object> getData(T entity) {
    List<Object> fieldsData = new ArrayList<>();

    this.fieldGetters.forEach((name, method) -> {
      fieldsData.add(this.invokeMethod(method, entity));
    });

    return fieldsData;
  }

  public List<Object> getDataWithId(T entity) {
    List<Object> fieldsData = new ArrayList<>(this.getData(entity));
    fieldsData.add(
            fieldsData.add(this.invokeMethod(this.idFieldGetter, entity)));

    return fieldsData;
  }

  private Object invokeMethod(Method method, T entity) {
    try {
      return method.invoke(entity);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
