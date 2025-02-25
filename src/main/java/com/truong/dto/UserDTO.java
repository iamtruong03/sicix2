package com.truong.dto;

import com.truong.entities.User;

public class UserDTO {
    private Long id;
    private String fullName;
    private String username;
    private String address;
    private String nameDepartment; // Thêm thuộc tính này

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.getId(),
            user.getFullName(),
            user.getUsername(),
            user.getAddress(),
            user.getDepartment() != null ? user.getDepartment().getNameDepartment() : null
        );
    }

    public UserDTO(Long id, String fullName, String username, String address, String nameDepartment) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.address = address;
        this.nameDepartment = nameDepartment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNameDepartment() {
        return nameDepartment;
    }

    public void setNameDepartment(String nameDepartment) {
        this.nameDepartment = nameDepartment;
    }
}
