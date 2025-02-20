package com.truong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.entities.Department;
import com.truong.repository.DepartmentReponsitory;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentReponsitory departmentReponsitory;
	
	public Department createDepartment(Department department) {
		if (department.getDepartmentId() != null) {
			
		}
		return departmentReponsitory.save(department);
	}
}
