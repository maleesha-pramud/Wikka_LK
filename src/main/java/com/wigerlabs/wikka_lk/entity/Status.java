package com.wigerlabs.wikka_lk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status")
@NamedQuery(name = "Status.findByValue",
        query = "FROM Status s WHERE s.value=:value")
public class Status extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "value", nullable = false, length = 20, unique = true)
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum Type {
        ACTIVE("active"),
        INACTIVE("inactive"),
        PENDING("pending"),
        BLOCKED("blocked"),
        VERIFIED("verified"),
        CANCELED("canceled"),
        COMPLETED("completed");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
