package ru.otus.model;

import jakarta.annotation.Nonnull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor(onConstructor_ = {@PersistenceCreator})
@Getter
@EqualsAndHashCode
@Table("addresses")
public class Address {
  @Id
  private final Long id;

  @Nonnull
  private final String street;

  public Address(String street) {
    this(null, street);
  }

  @Override
  public String toString() {
    return "Client{" +
            "id=" + id +
            "street=" + street +
            '}';
  }
}
