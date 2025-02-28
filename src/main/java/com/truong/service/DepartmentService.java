package com.truong.service;

import com.truong.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	
	// lấy phòng ban hiện tại + con cháu...
	public List<Map<String, Object>> getDepartmentList(Department department) {
        List<Department> listDepartment = new ArrayList<>();

        if (department != null) {
            listDepartment.add(department); // Thêm phòng ban hiện tại
            listDepartment.addAll(getAllSubDepartments(department)); // Thêm phòng ban con cháu
        } else {
            listDepartment.addAll(departmentRepository.findAll()); // Nếu không có phòng ban, lấy tất cả
        }

        // Chuyển đổi danh sách chỉ lấy departmentId và nameDepartment
        return listDepartment.stream()
                .map(dept -> {
                    Map<String, Object> deptMap = new HashMap<>();
                    deptMap.put("departmentId", dept.getDepartmentId());
                    deptMap.put("nameDepartment", dept.getNameDepartment());
                    return deptMap;
                })
                .collect(Collectors.toList());
    }

	// list user con, cháu, chắt
	public List<UserDTO> getUsersByDepartment(Long departmentId) {
	    Department parentDepartment = departmentRepository.findById(departmentId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + departmentId));

	    List<Department> subDepartments = getAllSubDepartments(parentDepartment);

	    List<Long> departmentIds = subDepartments.stream()
	            .map(Department::getDepartmentId)
	            .collect(Collectors.toList());

	    if (departmentIds.isEmpty()) {
	        return Collections.emptyList();
	    }

	    return userRepository.findByDepartmentIds(departmentIds).stream()
	            .map(user -> new UserDTO(
	                    user.getId(),
	                    user.getFullName(),
	                    user.getUserName(),
	                    user.getPassword(),
	                    user.getAddress(),
	                    user.getDepartment() != null ? user.getDepartment().getNameDepartment() : null, 
	                    user.getDepartment() != null ? user.getDepartment().getDepartmentId() : null 
	            ))
	            .collect(Collectors.toList());
	}


	public Map<String, Object> getDepartmentInfo(Long userId) {
	    User user = userRepository.findById(userId).orElse(null);
	    if (user == null) {
	        throw new RuntimeException("User không tồn tại");
	    }

	    List<Map<String, Object>> subDepartmentDetails = new ArrayList<>();
	    int totalUserCount = 0;

	    if (user.getDepartment() == null) {
	        List<Department> allDepartments = departmentRepository.findAll();

	        for (Department dept : allDepartments) {
	            int userCount = userRepository.countByDepartment(dept);
	            totalUserCount += userCount;

	            String parentDepartmentName = Optional.ofNullable(dept.getParentDepartment())
	                                                  .map(Department::getNameDepartment)
	                                                  .orElse("Không có");

	            Map<String, Object> subDeptInfo = new HashMap<>();
	            subDeptInfo.put("subDepartmentId", dept.getDepartmentId());
	            subDeptInfo.put("subDepartmentName", Objects.requireNonNullElse(dept.getNameDepartment(), "Không xác định"));
	            subDeptInfo.put("parentDepartmentName", parentDepartmentName);
	            subDeptInfo.put("userCount", userCount);

	            subDepartmentDetails.add(subDeptInfo);
	        }

	        Map<String, Object> response = new HashMap<>();
	        response.put("departmentId", null);
	        response.put("departmentName", "Tất cả phòng ban");
	        response.put("parentDepartmentName", "Không có");
	        response.put("userCount", 0);
	        response.put("totalUserCount", totalUserCount);
	        response.put("subDepartments", subDepartmentDetails);

	        return response;
	    }

	    Department department = user.getDepartment();
	    int userCount = userRepository.countByDepartment(department);
	    totalUserCount = userCount;

	    List<Department> subDepartments = getAllSubDepartments(department);
	    for (Department subDept : subDepartments) {
	        int subDeptUserCount = userRepository.countByDepartment(subDept);
	        totalUserCount += subDeptUserCount;

	        String parentDepartmentName = Optional.ofNullable(subDept.getParentDepartment())
	                                              .map(Department::getNameDepartment)
	                                              .orElse("Không có");

	        Map<String, Object> subDeptInfo = new HashMap<>();
	        subDeptInfo.put("subDepartmentId", subDept.getDepartmentId());
	        subDeptInfo.put("subDepartmentName", Objects.requireNonNullElse(subDept.getNameDepartment(), "Không xác định"));
	        subDeptInfo.put("parentDepartmentName", parentDepartmentName);
	        subDeptInfo.put("userCount", subDeptUserCount);

	        subDepartmentDetails.add(subDeptInfo);
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("departmentId", department.getDepartmentId());
	    response.put("departmentName", Objects.requireNonNullElse(department.getNameDepartment(), "Không xác định"));
	    response.put("parentDepartmentName", Optional.ofNullable(department.getParentDepartment())
	                                                 .map(Department::getNameDepartment)
	                                                 .orElse("Không có"));
	    response.put("userCount", userCount);
	    response.put("totalUserCount", totalUserCount);
	    response.put("subDepartments", subDepartmentDetails);

	    return response;
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
		User user = userRepository.findById(userId).orElse(null);
		Department department = departmentRepository.findById(departmentId).orElse(null);

		if (user == null || department == null) {
			return Map.of("success", false, "message", "Người dùng hoặc phòng ban không tồn tại");
		}

		if (!isUserAuthorizedToEdit(user, department)) {
			return Map.of("success", false, "message", "Bạn không có quyền sửa phòng ban này");
		}

		if (newDepartmentName == null || newDepartmentName.trim().isEmpty()) {
			return Map.of("success", false, "message", "Tên phòng ban không hợp lệ");
		}

		if (newParentId != null) {
			if (newParentId.equals(departmentId)) {
				return Map.of("success", false, "message", "Phòng ban không thể làm cha của chính nó");
			}

			Department newParentDepartment = departmentRepository.findById(newParentId).orElse(null);
			if (newParentDepartment == null) {
				return Map.of("success", false, "message", "Phòng ban cha mới không tồn tại");
			}

			// Kiểm tra nếu newParentId là con/cháu của department (tránh vòng lặp)
			List<Department> subDepartments = getAllSubDepartments(department);
			if (subDepartments.stream().anyMatch(subDept -> subDept.getDepartmentId().equals(newParentId))) {
				return Map.of("success", false, "message", "Không thể đặt phòng ban cha mới là con/cháu của chính nó");
			}

			// Kiểm tra nếu newParentId có nằm trong phạm vi quản lý của user không
			if (user.getDepartment() != null) {
				List<Department> userSubDepartments = getAllSubDepartments(user.getDepartment());
				userSubDepartments.add(user.getDepartment()); // Thêm chính phòng ban của user
				boolean isParentValid = userSubDepartments.stream()
						.anyMatch(dept -> dept.getDepartmentId().equals(newParentId));

				if (!isParentValid) {
					return Map.of("success", false, "message",
							"Bạn chỉ có thể đổi cha trong phạm vi phòng ban của mình");
				}
			}

			department.setParentDepartment(newParentDepartment);
		}

		department.setNameDepartment(newDepartmentName);
		departmentRepository.save(department);
		return Map.of("success", true, "message", "Cập nhật phòng ban thành công");
	}

	private boolean isUserAuthorizedToEdit(User user, Department department) {
		if (user.getDepartment() == null) {
			return true;
		}

		if (user.getDepartment().getDepartmentId().equals(department.getDepartmentId())) {
			return true;
		}
		List<Department> subDepartments = getAllSubDepartments(user.getDepartment());
		boolean hasPermission = subDepartments.stream()
				.anyMatch(subDept -> subDept.getDepartmentId().equals(department.getDepartmentId()));

		return hasPermission;
	}

	public Map<String, Object> deleteDepartment(Long userId, Long departmentId) {
		User user = userRepository.findById(userId).get();

		if (user.getDepartment() != null && user.getDepartment().getDepartmentId().equals(departmentId)) {
			return Map.of("success", false, "message", "Bạn không thể xóa phòng ban của chính mình");
		}
		Department department = departmentRepository.findById(departmentId).get();

		List<Department> subDepartments = getAllSubDepartments(department);

		if (hasUsersInDepartmentOrSub(department, subDepartments)) {
			return Map.of("success", false, "message", "Không thể xóa vì phòng ban hoặc phòng ban con còn nhân sự");
		}

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
