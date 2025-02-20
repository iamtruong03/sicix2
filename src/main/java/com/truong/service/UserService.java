package com.truong.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.entities.User;
import com.truong.exception.AppException;
import com.truong.exception.ErrorCode;
import com.truong.repository.DepartmentReponsitory;
import com.truong.repository.UserReponsitory;

@Service
public class UserService {
	@Autowired
	private UserReponsitory userReponsitory;
	@Autowired
	private DepartmentReponsitory departmentReponsitory;

	public User createUser(User user) {
		return userReponsitory.save(user);
	}

	public User updateUser(User user) {
		return userReponsitory.save(user);
	}

	public Boolean deleteUser(Long id) {
		userReponsitory.deleteById(id);
		return true;
	}

	public List<User> getAllUsers() {
		return userReponsitory.findAll();
	}

	public User getUserById(Long id) {
		return userReponsitory.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
	}

	public Boolean login(String userName, String password) {
		if(userName == null) {
			throw new AppException(ErrorCode.INVALID_USER);
		}
		if (password == null || password.isEmpty()) {
			throw new AppException(ErrorCode.INVALID_PASSWORD);
		}
		
		User user = userReponsitory.findByUserName(userName)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		return true;
	}

	public Boolean changePassword(Long id, String newPasword) {
		return true;
	}

//	// Hàm đệ quy để lấy danh sách tất cả phòng ban con từ department cha
//	private void getAllSubDepartments(Department department, List<Department> subDepartments) {
//		subDepartments.add(department);
//		List<Department> children = departmentReponsitory.findByParentId(department.getDepartmentId());
//		for (Department child : children) {
//			getAllSubDepartments(child, subDepartments);
//		}
//	}

//	// Lấy danh sách user không có phòng ban và user thuộc phòng ban con
//	public List<User> getUsersByDepartment(Long departmentId) {
//		List<User> users = new ArrayList<>();
//
//		// Lấy danh sách user không có phòng ban (department_id = NULL)
//		List<User> usersWithoutDepartment = userReponsitory.findByDepartmentIsNull();
//		users.addAll(usersWithoutDepartment);
//
//		// Lấy danh sách phòng ban con từ departmentId truyền vào
//		Department parentDepartment = departmentReponsitory.findById(departmentId)
//				.orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + departmentId));
//
//		List<Department> subDepartments = new ArrayList<>();
//		getAllSubDepartments(parentDepartment, subDepartments);
//
//		// Lấy danh sách departmentId của tất cả phòng ban con
//		List<Long> departmentIds = new ArrayList<>();
//		for (Department dept : subDepartments) {
//			departmentIds.add(dept.getDepartmentId());
//		}
//
//		// Lấy danh sách user thuộc các phòng ban con
//		List<User> usersInSubDepartments = userReponsitory.findByDepartmentIds(departmentIds);
//		users.addAll(usersInSubDepartments);
//
//		return users;
//	}


//	public Job addJob(User user ) {
//		Job job = new Job();
//		getJob(user, job);
//	}
//	
//	public void getJob(User user, Job job) {
//		job.set(user.get);
//	}

}
