package ru.otus.crm.model;

import ru.otus.crm.service.annotation.*;

public class Client {
    @Id
    private Long id;

    @Field
    private String name;

    public Client() {
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    @EntityConstructor
    public Client(
            @ConstructorParam(fieldName="id") Long id,
            @ConstructorParam(fieldName="name") String name) {
        this.id = id;
        this.name = name;
    }

    @FieldGetter(fieldName = "id", isIdField = 1)
    public Long getId() {
        return id;
    }

    @FieldGetter(fieldName = "name")
    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
