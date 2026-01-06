package com.wigerlabs.wikka_lk.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private int id;
    private String name;
    private String email;
    private String password;
    private String address;
    private String description = "";
    private int statusId = 3;
    private String status;
    private int userRoleId = 3;
    private String userRole;
    private String verificationCode;

    public UserDTO() {
    }

    public UserDTO(int id, String name, String email, String address, String description, int statusId, String status, int userRoleId, String userRole, String verificationCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.description = description;
        this.statusId = statusId;
        this.status = status;
        this.userRoleId = userRoleId;
        this.userRole = userRole;
        this.verificationCode = verificationCode;
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

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
