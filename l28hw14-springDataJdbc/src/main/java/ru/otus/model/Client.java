package ru.otus.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.annotation.Nonnull;

import java.util.Set;

@AllArgsConstructor(onConstructor_ = {@PersistenceCreator})
@Getter
@EqualsAndHashCode
@Table("clients")
public class Client implements Cloneable {
    @Id
    private final Long id;

    @Nonnull
    private final String name;

    @Nonnull
    @MappedCollection(idColumn = "client_id")
    private final Address address;

    @Nonnull
    @MappedCollection(idColumn = "client_id")
    private final Set<Phone> phones;

    public Client(String name, Address address, Set<Phone> phones) {
        this(null, name, address, phones);
    }

   @Override
   public Client clone() {
       return new Client(this.id, this.name, this.address, this.phones);
   }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phones=" + phones +
                '}';
    }
}
