package com.wigerlabs.wikka_lk.dto;

import java.io.Serializable;

public class ModelDTO implements Serializable {
    private int id;
    private String name;
    private int brandId;

    public ModelDTO() {
    }

    public ModelDTO(int id, String name, int brandId) {
        this.id = id;
        this.name = name;
        this.brandId = brandId;
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

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
}
