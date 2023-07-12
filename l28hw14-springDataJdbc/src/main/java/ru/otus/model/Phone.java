package ru.otus.model;

import jakarta.annotation.Nonnull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor(onConstructor_ = {@PersistenceCreator})
@Getter
@EqualsAndHashCode
@Table("phones")
public class Phone {
    @Id
    private final Long id;

    @Nonnull
    private final String number;

    public Phone(String number) {
        this(null, number);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", number='" + number + '\'' +
                '}';
    }
}
