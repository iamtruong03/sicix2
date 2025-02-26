package com.truong.service;

import com.truong.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.dto.UserDTO;
import com.truong.entities.Department;
import com.truong.entities.User;
import com.truong.repository.DepartmentRepository;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private UserRepository userRepository;

	// tìm phòng ban hiện tại
	public Department getDepartmentById(Long departmentId) {
		return departmentRepository.findById(departmentId).orElse(null);
	}

	// lấy phòng ban con, cháu, chắt
	public List<Department> getAllSubDepartments(Department department) {
		List<Department> subDepartments = new ArrayList<>();
		Queue<Department> queue = new LinkedList<>();
		queue.add(department);

		while (!queue.isEmpty()) {
			Department current = queue.poll();
			List<Department> children = departmentRepository.findByParentId(current.getDepartmentId());
			subDepartments.addAll(children);
			queue.addAll(children);
		}
		return subDepartments;
	}

	// Lấy thông tin phòng ban của người dùng
	public Map<String, Object> getDepartmentInfo(Long userId) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			throw new RuntimeException("User không tồn tại");
		}

		// Nếu user là admin (department_id = null), lấy toàn bộ phòng ban
		if (user.getDepartment() == null) {
			List<Department> allDepartments = departmentRepository.findAll();
			List<Map<String, Object>> departmentList = new ArrayList<>();

			for (Department dept : allDepartments) {
				int userCount = userRepository.countByDepartment(dept);
				departmentList.add(Map.of("departmentId", dept.getDepartmentId(), "departmentName",
						dept.getNameDepartment(), "parentDepartmentName",
						dept.getParentDepartment() != null ? dept.getParentDepartment().getNameDepartment()
								: "Không có",
						"userCount", userCount));
			}

			return Map.of("departments", departmentList); // Trả về danh sách toàn bộ phòng ban
		}

		// Nếu không phải admin, chỉ lấy thông tin phòng ban của user
		Department department = user.getDepartment();

		String departmentName = department.getNameDepartment();
		String parentDepartmentName = (department.getParentDepartment() != null)
				? department.getParentDepartment().getNameDepartment()
				: "Không có";

		List<Department> subDepartments = getAllSubDepartments(department);

		List<Map<String, Object>> subDepartmentDetails = new ArrayList<>();
		int userCount = userRepository.countByDepartment(department); // Nhân sự phòng ban hiện tại
		int totalUserCount = userCount;

		for (Department subDept : subDepartments) {
			int subDeptUserCount = userRepository.countByDepartment(subDept);
			totalUserCount += subDeptUserCount;

			Map<String, Object> subDeptInfo = Map.of("subDepartmentId", subDept.getDepartmentId(), "subDepartmentName",
					subDept.getNameDepartment(), "subparentDepartmentName",
					subDept.getParentDepartment() != null ? subDept.getParentDepartment().getNameDepartment()
							: "Không có",
					"userCounts", subDeptUserCount);
			subDepartmentDetails.add(subDeptInfo);
		}

		return Map.of("departmentId", department.getDepartmentId(), "departmentName", departmentName,
				"parentDepartmentName", parentDepartmentName, "userCount", userCount, "totalUserCount", totalUserCount,
				"subDepartments", subDepartmentDetails);
	}

	// list user con, cháu, chắt
	public List<UserDTO> getUsersByDepartment(Long departmentId) {
		// Lấy phòng ban cha từ ID
		Department parentDepartment = departmentRepository.findById(departmentId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + departmentId));

		// Lấy danh sách tất cả phòng ban con
		List<Department> subDepartments = getAllSubDepartments(parentDepartment);

		// Lấy danh sách ID của các phòng ban con
		List<Long> departmentIds = subDepartments.stream().map(Department::getDepartmentId)
				.collect(Collectors.toList());

		// Nếu không có phòng ban con => trả về danh sách rỗng
		if (departmentIds.isEmpty()) {
			return Collections.emptyList();
		}

		// Lấy danh sách user thuộc các phòng ban con và chuyển đổi sang DTO
		return userRepository.findByDepartmentIds(departmentIds).stream()
				.map(user -> new UserDTO(user.getId(), user.getFullName(), user.getUserName(), user.getAddress(),
						user.getDepartment() != null ? user.getDepartment().getNameDepartment() : null

				)).collect(Collectors.toList());

	}

	// thêm phòng ban
	public Map<String, Object> createDepartment(Long userId, String departmentName, Long parentId) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			return Map.of("success", false, "message", "Người dùng không tồn tại");
		}

		User user = optionalUser.get();
		boolean isAdmin = (user.getDepartment() == null);

		if (!isAdmin && parentId == null) {
			return Map.of("success", false, "message", "Bạn không có quyền tạo phòng ban cấp cao nhất");
		}

		Department parentDepartment = null;
		if (parentId != null) {
			parentDepartment = departmentRepository.findById(parentId).orElse(null);
			if (parentDepartment == null) {
				return Map.of("success", false, "message", "Phòng ban cha không tồn tại");
			}
		}

		if (!isAdmin) {
			if (user.getDepartment() == null) {
				return Map.of("success", false, "message",
						"Bạn không thuộc phòng ban nào nên không thể tạo phòng ban con");
			}

			// Lấy danh sách tất cả phòng ban con, cháu, chắt của user
			List<Department> allowedDepartments = getAllSubDepartments(user.getDepartment());
			allowedDepartments.add(user.getDepartment());

			// Kiểm tra nếu user không có quyền tạo phòng ban con
			if (parentDepartment != null && !allowedDepartments.contains(parentDepartment)) {
				return Map.of("success", false, "message", "Bạn không có quyền tạo phòng ban ở vị trí này");
			}
		}

		// Tạo phòng ban mới
		Department newDepartment = new Department();
		newDepartment.setNameDepartment(departmentName);
		newDepartment.setParentDepartment(parentDepartment);

		departmentRepository.save(newDepartment);
		return Map.of("success", true, "message", "Phòng ban đã được tạo thành công");
	}

	private boolean isChildDepartment(Department parentDept, Department childDept) {
		if (childDept == null)
			return false;
		while (childDept.getParentDepartment() != null) {
			if (childDept.getParentDepartment().equals(parentDept)) {
				return true;
			}
			childDept = childDept.getParentDepartment();
		}
		return false;
	}

	// sửa phòng ban
	public Map<String, Object> updateDepartment(Long userId, Long departmentId, String newDepartmentName,
			Long newParentId) {
		// Kiểm tra User có tồn tại không
		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			return Map.of("success", false, "message", "Người dùng không tồn tại");
		}

		// Kiểm tra phòng ban cần sửa có tồn tại không
		Department department = departmentRepository.findById(departmentId).orElse(null);
		if (department == null) {
			return Map.of("success", false, "message", "Phòng ban không tồn tại");
		}

		// Kiểm tra quyền sửa phòng ban
		if (!isUserAuthorizedToEdit(user, department)) {
			return Map.of("success", false, "message", "Bạn không có quyền sửa phòng ban này");
		}

		// Kiểm tra tên mới hợp lệ
		if (newDepartmentName == null || newDepartmentName.trim().isEmpty()) {
			return Map.of("success", false, "message", "Tên phòng ban không hợp lệ");
		}

		// Nếu có phòng ban cha mới
		if (newParentId != null) {
			// Không cho phép phòng ban trở thành con của chính nó
			if (newParentId.equals(departmentId)) {
				return Map.of("success", false, "message", "Phòng ban không thể làm cha của chính nó");
			}

			// Lấy phòng ban cha mới
			Department newParentDepartment = departmentRepository.findById(newParentId).orElse(null);
			if (newParentDepartment == null) {
				return Map.of("success", false, "message", "Phòng ban cha mới không tồn tại");
			}

			// Lấy danh sách các phòng ban con từ hàm getAllSubDepartments()
			List<Department> allowedParentDepartments = getAllSubDepartments(department);

			// Kiểm tra nếu newParentId không nằm trong danh sách được phép
			boolean isValidParent = allowedParentDepartments.stream()
					.anyMatch(subDept -> subDept.getDepartmentId().equals(newParentId));

			if (!isValidParent) {
				return Map.of("success", false, "message",
						"Phòng ban cha mới không hợp lệ. Chỉ có thể chọn từ danh sách phòng ban con hợp lệ.");
			}

			// Kiểm tra nếu newParentDepartment là con/cháu của department thì không cho
			// phép cập nhật
			List<Department> subDepartments = getAllSubDepartments(department);
			if (subDepartments.stream().anyMatch(subDept -> subDept.getDepartmentId().equals(newParentId))) {
				return Map.of("success", false, "message",
						"Không thể chuyển phòng ban thành con của chính nó hoặc con/cháu của nó");
			}

			if (department.getParentDepartment() != null && newParentDepartment.getParentDepartment() != null) {
				if (department.getParentDepartment().getDepartmentId()
						.equals(newParentDepartment.getParentDepartment().getDepartmentId())) {
					return Map.of("success", false, "message",
							"Không thể chuyển phòng ban thành ngang cấp với phòng ban cũ");
				}
			}

			department.setParentDepartment(newParentDepartment);
		}

		department.setNameDepartment(newDepartmentName);

		// Lưu thay đổi
		departmentRepository.save(department);
		return Map.of("success", true, "message", "Cập nhật phòng ban thành công");
	}

	private boolean isUserAuthorizedToEdit(User user, Department department) {
		// Admin có quyền sửa tất cả
		if (user.getDepartment() == null) {
			return true;
		}

		// Lấy danh sách tất cả phòng ban con của phòng ban của user
		List<Department> subDepartments = getAllSubDepartments(department);

		// Kiểm tra nếu phòng ban cần sửa nằm trong danh sách phòng ban con
		return subDepartments.contains(department);
	}

	// xóa phòng ban:chỉ user department_id Null xóa hoặc userParentId xóa và Không
	// cho phép xóa nếu còn user
	public Map<String, Object> deleteDepartment(Long userId, Long departmentId) {
		User user = userRepository.findById(userId).get();

		if (user.getDepartment() != null && user.getDepartment().getDepartmentId().equals(departmentId)) {
			return Map.of("success", false, "message", "Bạn không thể xóa phòng ban của chính mình");
		}
		Department department = departmentRepository.findById(departmentId).get();

		List<Department> subDepartments = getAllSubDepartments(department);

		// Kiểm tra nếu phòng ban hoặc phòng ban con nào còn nhân sự
		if (hasUsersInDepartmentOrSub(department, subDepartments)) {
			return Map.of("success", false, "message", "Không thể xóa vì phòng ban hoặc phòng ban con còn nhân sự");
		}

		// Xóa tất cả phòng ban con trước khi xóa phòng ban chính
		departmentRepository.deleteAll(subDepartments);
		departmentRepository.delete(department);

		return Map.of("success", true, "message", "Đã xóa phòng ban thành công");
	}

	// Kiểm tra phòng ban hoặc phòng ban con có nhân sự không
	private boolean hasUsersInDepartmentOrSub(Department department, List<Department> subDepartments) {
		if (userRepository.countByDepartment(department) > 0)
			return true;
		for (Department subDept : subDepartments) {
			if (userRepository.countByDepartment(subDept) > 0)
				return true;
		}
		return false;
	}

}
