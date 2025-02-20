package com.truong.entities;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.truong.entities.Department;
import com.truong.entities.User;

import jakarta.persistence.*;

@Entity
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long departmentId;

	@Column(nullable = false, unique = true)
	private String nameDepartment;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	private Department parentDepartment;

	@OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.PERSIST)
	private List<Department> subDepartments = new ArrayList<>();

	@OneToMany(mappedBy = "department")
	@JsonManagedReference
	private List<User> users = new ArrayList<>();

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getNameDepartment() {
		return nameDepartment;
	}

	public void setNameDepartment(String nameDepartment) {
		this.nameDepartment = nameDepartment;
	}

	public Department getParentDepartment() {
		return parentDepartment;
	}

	public void setParentDepartment(Department parentDepartment) {
		this.parentDepartment = parentDepartment;
	}

	public List<Department> getSubDepartments() {
		return subDepartments;
	}

	public void setSubDepartments(List<Department> subDepartments) {
		this.subDepartments = subDepartments;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
