package com.truong.controller;

import com.truong.dto.UserDTO;
import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.repository.UserRepository;
import com.truong.service.DepartmentService;
import com.truong.service.JobService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.truong.entities.User;
import com.truong.exception.AppException;
import com.truong.exception.ErrorCode;
import com.truong.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private JobService jobService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
			HttpSession session) {
		String userName = loginRequest.get("userName");
		String password = loginRequest.get("password");

		if (userName == null || password == null) {
			throw new AppException(ErrorCode.INVALID_USER);
		}

		User user = userRepository.findByUserName(userName)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		if (!password.equals(user.getPassword())) {
			throw new AppException(ErrorCode.INVALID_PASSWORD);
		}

		// Lưu userId vào session
		session.setAttribute("userId", user.getId());

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("userId", user.getId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
		session.invalidate(); // Xóa session
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Đăng xuất thành công");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/createUser")
	public ResponseEntity<?> createUser(@RequestBody User user, HttpSession session) {
		if (user == null) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Dữ liệu user không hợp lệ!"));
		}

		Long currentUserId = (Long) session.getAttribute("userId");
		if (currentUserId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "User not logged in"));
		}

		User currentUser = userService.getUserById(currentUserId);
		if (currentUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "Người dùng không tồn tại!"));
		}

		try {
			userService.createUser(user, currentUser);
			return ResponseEntity.ok(Map.of("success", true, "message", "Tạo user thành công!"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@PostMapping("/createJob")
	public ResponseEntity<?> createJob(@RequestBody Map<String, Object> requestData, HttpSession session) {
		try {
			Long createdUserId = (Long) session.getAttribute("userId");

			if (createdUserId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("success", false, "message", "User not logged in"));
			}

			String jobName = (String) requestData.get("jobName");
			LocalDate deadline = LocalDate.parse((String) requestData.get("deadline"));
			List<Integer> executedUserIds = (List<Integer>) requestData.get("executedUserIds");
			List<Long> executedUserIdsLong = executedUserIds.stream().map(Integer::longValue)
					.collect(Collectors.toList());

			Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserIdsLong);

			// Trả về JSON đúng định dạng mà FE mong đợi
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(Map.of("success", true, "message", "Tạo công việc thành công!", "job", job));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@PostMapping("/createApproveJob")
	public ResponseEntity<?> createApproveJob(@RequestParam String jobName, @RequestParam LocalDate deadline,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
		}
		try {
			Job job = jobService.createApproveJob(userId, jobName, deadline);
			return ResponseEntity.ok(job);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/update-status")
	public ResponseEntity<?> updateJobStatus(@RequestParam Long jobId, @RequestParam Long newStatusId,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		try {
			Job updatedJob = jobService.updateJobStatus(jobId, userId, newStatusId);
			return ResponseEntity.ok(updatedJob);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/my-jobs")
	public ResponseEntity<?> getJobsByExecutedId(HttpSession session) {
		Long id = (Long) session.getAttribute("userId");

		if (id == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn chưa đăng nhập!");
		}

		try {
			List<Map<String, Object>> jobs = jobService.getJobsByExecutedId(id);
			return ResponseEntity.ok(jobs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
		}
	}

	@GetMapping("/subordinates-jobs")
	public ResponseEntity<?> getJobsOfSubordinates(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User chưa đăng nhập!");
		}

		try {
			List<Map<String, Object>> jobs = jobService.getJobsOfSubordinates(userId);
			return ResponseEntity.ok(jobs);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// thống kê phòng ban con
	@GetMapping("/subordinate-statistics")
	public ResponseEntity<Map<String, Long>> getSubordinateJobStatistics(@SessionAttribute("userId") Long userId) {
		Map<String, Long> statistics = jobService.countJobsByStatusForSubordinates(userId);
		return ResponseEntity.ok(statistics);
	}

	// thống kê của user
	@GetMapping("/userStatistics")
	public ResponseEntity<Map<String, Long>> getUserJobStatistics(@SessionAttribute("userId") Long userId) {
		Map<String, Long> statistic = jobService.countJobsByExecutedUser(userId);
		return ResponseEntity.ok(statistic);
	}

	// xem thông tin cá nhân
	@GetMapping("/userInfo")
	public ResponseEntity<UserDTO> getInfoUser(@SessionAttribute("userId") Long userId) {
		try {
			return ResponseEntity.ok(userService.getUserDTOById(userId));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// xem thông tin user
	@GetMapping("/sub-users")
	public ResponseEntity<List<UserDTO>> getSubUsers(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		List<UserDTO> subUsers = userService.getSubUsersByUserId(userId);
		return ResponseEntity.ok(subUsers);
	}

	// sửa user
	@PutMapping("/updateUser/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @SessionAttribute("userId") Long currentUserId,
			@RequestBody UserDTO userDTO) {
		userService.updateUser(userId, currentUserId, userDTO);
		return ResponseEntity.ok(Collections.singletonMap("message", "Cập nhật thành công!"));
	}

	// xóa user
	@DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId, @SessionAttribute("userId") Long sessionUserId) {
		try {
			userService.deleteUser(userId, sessionUserId);
			return ResponseEntity.ok().body(Map.of("message", "User deleted successfully"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Internal server error"));
		}
	}

	// lấy list phòng ban: bao gồm phòng ban con và chính nó
	@GetMapping("/department/list")
	public ResponseEntity<?> getDepartmentList(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "Người dùng chưa đăng nhập"));
		}

		User user = userRepository.findById(userId).orElse(null);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("success", false, "message", "Không tìm thấy người dùng"));
		}

		Department userDepartment = user.getDepartment();
		List<Map<String, Object>> departments = departmentService.getDepartmentList(userDepartment);

		return ResponseEntity.ok(Map.of("success", true, "data", departments));
	}

	// xem danh sách phòng ban
	@GetMapping("/my-department")
	public ResponseEntity<?> getDepartmentInfo(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "Người dùng chưa đăng nhập"));
		}
		Map<String, Object> departmentInfo = departmentService.getDepartmentInfo(userId);

		if (departmentInfo.containsKey("message")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("success", false, "message", departmentInfo.get("message")));
		}
		return ResponseEntity.ok(Map.of("success", true, "data", departmentInfo));
	}

	// Thêm phòng ban
	@PostMapping("/department/createDepartment")
	public ResponseEntity<?> createDepartment(@RequestBody Map<String, Object> request, HttpSession session) {
		// Lấy userId từ session
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "Người dùng chưa đăng nhập"));
		}
		String departmentName = (String) request.get("departmentName");
		if (departmentName == null || departmentName.trim().isEmpty()) {
			return ResponseEntity.badRequest()
					.body(Map.of("success", false, "message", "Tên phòng ban không được để trống"));
		}
		Long parentId = null;
		if (request.containsKey("parentId") && request.get("parentId") instanceof Number) {
			parentId = ((Number) request.get("parentId")).longValue();
		}
		Map<String, Object> response = departmentService.createDepartment(userId, departmentName, parentId);
		if (Boolean.TRUE.equals(response.get("success"))) {
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}

	// Sửa phòng ban
	@PutMapping("/department/update/{departmentId}")
	public ResponseEntity<?> updateDepartment(@PathVariable Long departmentId, @RequestBody Map<String, Object> request,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		String newDepartmentName = (String) request.get("newDepartmentName");
		Long newParentId = request.get("newParentId") != null ? ((Number) request.get("newParentId")).longValue()
				: null;

		Map<String, Object> response = departmentService.updateDepartment(userId, departmentId, newDepartmentName,
				newParentId);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/department/delete/{departmentId}")
	public ResponseEntity<?> deleteDepartment(@PathVariable Long departmentId, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");

		Map<String, Object> response = departmentService.deleteDepartment(userId, departmentId);
		boolean success = (boolean) response.get("success");

		if (!success) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		return ResponseEntity.ok(response);
	}

}
