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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.truong.dto.UserDTO;
import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.entities.User;
import com.truong.exception.AppException;
import com.truong.exception.ErrorCode;
import com.truong.repository.DepartmentRepository;
import com.truong.repository.JobExecutorsRepository;
import com.truong.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private JobExecutorsRepository jobExecutorsRepository;
	@Autowired
	private DepartmentRepository departmentRepository;

	// lấy phòng ban của người dùng
	public Long getDepartmentIdByUserId(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));
		return user.getDepartment().getDepartmentId();
	}

	// xem user con
	public List<UserDTO> getSubUsersByUserId(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getDepartment() == null) {
			return userRepository.findAll().stream().map(UserDTO::fromEntity).collect(Collectors.toList());
		} else {
			Long departmentId = getDepartmentIdByUserId(userId);
			return departmentService.getUsersByDepartment(departmentId);
		}
	}

	// tạo TK
	public void createUser(User user, User currentUser) {
		if (user == null) {
			throw new IllegalArgumentException("User không được để trống!");
		}

		Department newUserDepartment = user.getDepartment();
		Department creatorDepartment = currentUser.getDepartment();

		if (creatorDepartment == null) {
			userRepository.save(user);
			return;
		}

		if (newUserDepartment == null) {
			throw new RuntimeException("Phòng ban của nhân viên không được để trống!");
		}
		List<Department> allowedDepartments = departmentService.getAllSubDepartments(creatorDepartment);

		boolean hasPermission = allowedDepartments.stream()
				.anyMatch(dept -> dept.getDepartmentId().equals(newUserDepartment.getDepartmentId()));
		if (!hasPermission) {
			throw new RuntimeException("Bạn không có quyền tạo nhân viên trong phòng ban này!");
		}
		userRepository.save(user);
	}

	// lấy user
	public User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User không tồn tại!"));
	}

	public UserDTO getUserDTOById(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		return UserDTO.fromEntity(user);
	}

	// update user
	@Transactional
	public void updateUser(Long userId, Long currentUserId, UserDTO userDTO) {
		User currentUser = userRepository.findById(currentUserId)
				.orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

		User userToUpdate = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Người dùng cần sửa không tồn tại"));

		if (currentUser.getId().equals(userToUpdate.getId())) {
			userToUpdate.setFullName(userDTO.getFullName());
			userToUpdate.setPassword(userDTO.getPassword());
			userToUpdate.setAddress(userDTO.getAddress());
		} else if (currentUser.getDepartment() == null) {
			userToUpdate.setFullName(userDTO.getFullName());
			userToUpdate.setUserName(userDTO.getUserName());
			userToUpdate.setPassword(userDTO.getPassword());
			userToUpdate.setAddress(userDTO.getAddress());

			if (userDTO.getDepartmentId() != null) {
				Department newDepartment = departmentRepository.findById(userDTO.getDepartmentId())
						.orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại"));
				validateDepartmentChange(userToUpdate.getDepartment(), newDepartment, currentUser);
				userToUpdate.setDepartment(newDepartment);
			}
		}
		else if (this.getSubUsersByUserId(currentUserId).stream()
				.anyMatch(u -> u.getId().equals(userToUpdate.getId()))) {
			userToUpdate.setFullName(userDTO.getFullName());
			userToUpdate.setAddress(userDTO.getAddress());
			userToUpdate.setPassword(userDTO.getPassword());

			if (userDTO.getDepartmentId() != null) {
				Department newDepartment = departmentRepository.findById(userDTO.getDepartmentId())
						.orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại"));
				validateDepartmentChange(userToUpdate.getDepartment(), newDepartment, currentUser);
				userToUpdate.setDepartment(newDepartment);
			}
		} else {
			throw new RuntimeException("Bạn không có quyền cập nhật thông tin người dùng này!");
		}
		userRepository.save(userToUpdate);
	}

	private void validateDepartmentChange(Department currentDepartment, Department newDepartment, User currentUser) {
		if (currentDepartment != null) {
			List<Department> subDepartments = departmentService.getAllSubDepartments(currentDepartment);
			if (subDepartments.stream()
					.anyMatch(subDept -> subDept.getDepartmentId().equals(newDepartment.getDepartmentId()))) {
				throw new RuntimeException("Không thể đặt phòng ban cha mới là con/cháu của chính nó");
			}
		}

		if (currentUser.getDepartment() != null) {
			List<Department> userSubDepartments = departmentService.getAllSubDepartments(currentUser.getDepartment());
			userSubDepartments.add(currentUser.getDepartment());
			boolean isParentValid = userSubDepartments.stream()
					.anyMatch(dept -> dept.getDepartmentId().equals(newDepartment.getDepartmentId()));

			if (!isParentValid) {
				throw new RuntimeException("Bạn chỉ có thể đổi phòng ban trong phạm vi quản lý của mình");
			}
		}
	}

	// xóa user
	public void deleteUser(Long userId, Long currentUserId) {
		User currentUser = userRepository.findById(currentUserId)
				.orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

		User userToDelete = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Người dùng cần xóa không tồn tại"));
		if (currentUser.getDepartment() == null) {
			deleteUserWithConstraints(userToDelete);
			return;
		}
		if (this.getSubUsersByUserId(currentUserId).stream().noneMatch(u -> u.getId().equals(userId))) {
			throw new RuntimeException("Bạn không có quyền xóa user này");
		}
		deleteUserWithConstraints(userToDelete);
	}

	// kiểm tra điều kiện xóa
	private void deleteUserWithConstraints(User userToDelete) {
		Long userId = userToDelete.getId();

		if (jobRepository.existsByApproverId(userId)) {
			throw new RuntimeException("Không thể xóa user vì user này là người phê duyệt công việc");
		}
		if (jobExecutorsRepository.existsByUserId(userId)) {
			throw new RuntimeException("Không thể xóa user vì user này đang thực hiện công việc");
		}

		userRepository.delete(userToDelete);
	}

}
