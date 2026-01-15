package com.wigerlabs.wikka_lk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
@NamedQuery(name = "Category.findById", query = "FROM Category c WHERE c.id=:id")
@NamedQuery(name = "Category.findByName", query = "FROM Category c WHERE c.name=:name")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

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

