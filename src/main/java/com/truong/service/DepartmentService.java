package com.truong.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.entities.Department;
import com.truong.entities.User;
import com.truong.repository.DepartmentReponsitory;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentReponsitory departmentReponsitory;
	
	// xem user theo phòng ban con
	public List<User> getUsersByDepartment(Long departmentId) {
	    return departmentReponsitory.findByDepartmentId(departmentId)
	            .map(Department::getUsers)
	            .orElse(Collections.emptyList());
	}


	// Lấy phòng ban cha
	public Department getParent(Long departmentId) {
		Optional<Department> department = departmentReponsitory.findByDepartmentId(departmentId);
		return department.map(Department::getParentDepartment).orElse(null);
	}

	// Lấy danh sách phòng ban con trực tiếp
	public List<Department> getChildren(Long departmentId) {
		return departmentReponsitory.findByDepartmentId(departmentId).map(departmentReponsitory::findByParentDepartment)
				.orElse(Collections.emptyList());
	}

	// Đệ quy tìm tất cả hậu duệ (con, cháu, chắt)
	public List<Department> getDescendants(Long departmentId) {
		List<Department> descendants = new ArrayList<>();
		findDescendants(departmentId, descendants);
		return descendants;
	}

	private void findDescendants(Long parentId, List<Department> descendants) {
		Optional<Department> departmentOpt = departmentReponsitory.findByDepartmentId(parentId);
		if (departmentOpt.isPresent()) {
			List<Department> children = departmentReponsitory.findByParentDepartment(departmentOpt.get());
			for (Department child : children) {
				descendants.add(child);
				findDescendants(child.getDepartmentId(), descendants);
			}
		}
	}

}
