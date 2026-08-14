package com.wigerlabs.wikka_lk.dto;

import java.io.Serializable;

public class ProductConditionDTO implements Serializable {
    private int id;
    private String name;

    public ProductConditionDTO() {
    }

    public ProductConditionDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
