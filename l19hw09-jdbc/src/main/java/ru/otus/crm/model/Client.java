package ru.otus.crm.model;

import ru.otus.crm.service.annotation.*;

public class
Client {
    @Id
    private Long id;

    private String name;

    public Client() {
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Accessor(fieldName = "id", isIdField = 1, type = AccessorType.GETTER)
    public Long getId() {
        return id;
    }

    @Accessor(fieldName = "name", type = AccessorType.GETTER)
    public String getName() {
        return name;
    }

    @Accessor(fieldName = "id", type = AccessorType.SETTER)
    public void setId(Long id) {
        this.id = id;
    }

    @Accessor(fieldName = "name", type = AccessorType.SETTER)
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
