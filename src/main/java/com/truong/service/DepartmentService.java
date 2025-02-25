package com.truong.service;

import com.truong.repository.UserReponsitory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.dto.UserDTO;
import com.truong.entities.Department;
import com.truong.entities.User;
import com.truong.repository.DepartmentReponsitory;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentReponsitory departmentReponsitory;
	@Autowired
	private UserReponsitory userReponsitory;

	private void getAllSubDepartments(Department department, List<Department> subDepartments) {
		subDepartments.add(department);
		List<Department> children = departmentReponsitory.findByParentId(department.getDepartmentId());
		for (Department child : children) {
			getAllSubDepartments(child, subDepartments);
		}
	}

//	// Lấy danh sách user thuộc phòng ban con
//	public List<User> getUsersByDepartment(Long departmentId) {
//		// Lấy phòng ban cha từ ID
//		Department parentDepartment = departmentReponsitory.findById(departmentId)
//				.orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + departmentId));
//
//		// Lấy danh sách tất cả phòng ban con (KHÔNG bao gồm phòng ban chính)
//		List<Department> subDepartments = new ArrayList<>();
//		getAllSubDepartments(parentDepartment, subDepartments);
//		subDepartments.remove(parentDepartment); // Loại bỏ phòng ban chính
//
//		// Lấy danh sách ID của các phòng ban con
//		List<Long> departmentIds = subDepartments.stream()
//				.map(Department::getDepartmentId)
//				.collect(Collectors.toList());
//
//		// Nếu không có phòng ban con => trả về danh sách rỗng
//		if (departmentIds.isEmpty()) {
//			return Collections.emptyList();
//		}
//
//		// Lấy danh sách user thuộc các phòng ban con
//		return userReponsitory.findByDepartmentIds(departmentIds);
//	}
	public List<UserDTO> getUsersByDepartment(Long departmentId) {
		// Lấy phòng ban cha từ ID
		Department parentDepartment = departmentReponsitory.findById(departmentId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + departmentId));

		// Lấy danh sách tất cả phòng ban con (KHÔNG bao gồm phòng ban chính)
		List<Department> subDepartments = new ArrayList<>();
		getAllSubDepartments(parentDepartment, subDepartments);
		subDepartments.remove(parentDepartment); // Loại bỏ phòng ban chính

		// Lấy danh sách ID của các phòng ban con
		List<Long> departmentIds = subDepartments.stream().map(Department::getDepartmentId)
				.collect(Collectors.toList());

		// Nếu không có phòng ban con => trả về danh sách rỗng
		if (departmentIds.isEmpty()) {
			return Collections.emptyList();
		}

		// Lấy danh sách user thuộc các phòng ban con và chuyển đổi sang DTO
		return userReponsitory.findByDepartmentIds(departmentIds).stream()
				.map(user -> new UserDTO(user.getId(), user.getFullName(), user.getUsername(), user.getAddress(),
						user.getDepartment() != null ? user.getDepartment().getNameDepartment() : null 
																									
																										
				)).collect(Collectors.toList());

	}

//	// Lấy phòng ban cha
//	public Department getParent(Long departmentId) {
//		Optional<Department> department = departmentReponsitory.findByDepartmentId(departmentId);
//		return department.map(Department::getParentDepartment).orElse(null);
//	}
//
//	// Lấy danh sách phòng ban con trực tiếp
//	public List<Department> getChildren(Long departmentId) {
//		return departmentReponsitory.findByDepartmentId(departmentId).map(departmentReponsitory::findByParentDepartment)
//				.orElse(Collections.emptyList());
//	}
//
//	// Đệ quy tìm tất cả hậu duệ (con, cháu, chắt)
//	public List<Department> getDescendants(Long departmentId) {
//		List<Department> descendants = new ArrayList<>();
//		findDescendants(departmentId, descendants);
//		return descendants;
//	}
//
//	private void findDescendants(Long parentId, List<Department> descendants) {
//		Optional<Department> departmentOpt = departmentReponsitory.findByDepartmentId(parentId);
//		if (departmentOpt.isPresent()) {
//			List<Department> children = departmentReponsitory.findByParentDepartment(departmentOpt.get());
//			for (Department child : children) {
//				descendants.add(child);
//				findDescendants(child.getDepartmentId(), descendants);
//			}
//		}
//	}

}
