package ru.otus.crm.model;

import ru.otus.crm.service.annotation.*;

public class Manager {
    @Id
    private Long no;
    @Field
    private String label;
    @Field
    private String param1;

    public Manager() {
    }

    public Manager(String label) {
        this.label = label;
    }

    @EntityConstructor
    public Manager(
            @ConstructorParam(fieldName="no") Long no,
            @ConstructorParam(fieldName="label") String label,
            @ConstructorParam(fieldName="param1") String param1) {
        this.no = no;
        this.label = label;
        this.param1 = param1;
    }

    @FieldGetter(fieldName = "no", isIdField = 1)
    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    @FieldGetter(fieldName = "label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @FieldGetter(fieldName = "param1")
    public String getParam1() {
        return param1;
    }

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
