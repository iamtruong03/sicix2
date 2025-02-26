package com.truong.service;

import com.truong.repository.JobRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.dto.UserDTO;
import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.entities.User;
import com.truong.exception.AppException;
import com.truong.exception.ErrorCode;
import com.truong.repository.DepartmentRepository;
import com.truong.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private DepartmentService departmentService;

	public Long getDepartmentIdByUserId(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
		return user.getDepartment().getDepartmentId();
	}

	// Lấy danh sách user con theo userId
	public List<UserDTO> getSubUsersByUserId(Long userId) {
		Long departmentId = getDepartmentIdByUserId(userId);
		return departmentService.getUsersByDepartment(departmentId);
	}

	// tạo TK
	public void createUser(User user, User currentUser) {
		if (user == null) {
			throw new IllegalArgumentException("User không được để trống!");
		}

		Department newUserDepartment = user.getDepartment();
		Department creatorDepartment = currentUser.getDepartment();

		// Nếu currentUser là admin cấp cao (không thuộc phòng ban nào) -> có quyền tạo
		// user
		if (creatorDepartment == null) {
			userRepository.save(user);
			return;
		}

		// Nếu user mới không có phòng ban -> Không được phép tạo
		if (newUserDepartment == null) {
			throw new RuntimeException("Phòng ban của nhân viên không được để trống!");

		}

		// Lấy tất cả phòng ban con, cháu, chắt của phòng ban hiện tại
		List<Department> allowedDepartments = departmentService.getAllSubDepartments(creatorDepartment);

		// Kiểm tra nếu phòng ban của user mới thuộc danh sách phòng ban con, cháu, chắt
		boolean hasPermission = allowedDepartments.stream()
				.anyMatch(dept -> dept.getDepartmentId().equals(newUserDepartment.getDepartmentId()));

		if (!hasPermission) {
			throw new RuntimeException("Bạn không có quyền tạo nhân viên trong phòng ban này!");
		}

		userRepository.save(user);
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại!"));
	}

	// sửa TK

	// xóa TK

	// chỉnh sửa phòng ban

	// đổi MK

}
