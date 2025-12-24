package com.wigerlabs.wikka_lk.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private int id;
    private String name;
    private String email;
    private String password;
    private String address;
    private String description;
    private int statusId = 3;
    private int userRoleId = 3;
    private int sellerBankDetailsId;

    public UserDTO() {
    }

    public UserDTO(int id, String name, String email, String password, String address, String description) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.description = description;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }

    public int getSellerBankDetailsId() {
        return sellerBankDetailsId;
    }

    public void setSellerBankDetailsId(int sellerBankDetailsId) {
        this.sellerBankDetailsId = sellerBankDetailsId;
    }
}
