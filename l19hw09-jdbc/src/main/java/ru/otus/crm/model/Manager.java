package ru.otus.crm.model;

import ru.otus.crm.service.annotation.*;

public class Manager {
    @Id
    private Long no;
    private String label;
    private String param1;

    public Manager() {
    }

    public Manager(String label) {
        this.label = label;
    }

    public Manager(Long no, String label, String param1) {
        this.no = no;
        this.label = label;
        this.param1 = param1;
    }

    @Accessor(fieldName = "no", isIdField = 1, type = AccessorType.GETTER)
    public Long getNo() {
        return no;
    }

    @Accessor(fieldName = "no", type = AccessorType.SETTER)
    public void setNo(Long no) {
        this.no = no;
    }

    @Accessor(fieldName = "label", type = AccessorType.GETTER)
    public String getLabel() {
        return label;
    }

    @Accessor(fieldName = "label", type = AccessorType.SETTER)
    public void setLabel(String label) {
        this.label = label;
    }

    @Accessor(fieldName = "param1", type = AccessorType.GETTER)
    public String getParam1() {
        return param1;
    }

    @Accessor(fieldName = "param1", type = AccessorType.SETTER)
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "no=" + no +
                ", label='" + label + '\'' +
                '}';
    }
}
