package com.truong.dto;

import com.truong.entities.User;

public class UserDTO {
    private Long id;
    private String fullName;
    private String userName;
    private String address;
    private String nameDepartment; // Thêm thuộc tính này

    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
            user.getId(),
            user.getFullName(),
            user.getUserName(),
            user.getAddress(),
            user.getDepartment() != null ? user.getDepartment().getNameDepartment() : null
        );
    }

    public UserDTO(Long id, String fullName, String userName, String address, String nameDepartment) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
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


    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
