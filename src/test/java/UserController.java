package com.truong.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

//
//	    @GetMapping("/sub-department")
//	    public ResponseEntity<List<User>> getUsersInSubDepartments(HttpSession session) {
//	        Long userId = (Long) session.getAttribute("userId");
//	        User user = userReponsitory.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
//	        Department userDepartment = user.getDepartment();
//	        if (userDepartment == null) {
//	            return ResponseEntity.ok(Collections.emptyList());
//	        }
//	        List<User> users = departmentService.getUsersByDepartment(userDepartment.getDepartmentId());
//	        return ResponseEntity.ok(users);
//	    }

	@PostMapping("/createJob")
	public ResponseEntity<?> createJob(@RequestBody Map<String, Object> requestData, HttpSession session) {
		try {
			// Lấy userId từ session
			Long createdUserId = (Long) session.getAttribute("userId");

			if (createdUserId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
			}

			// Trích xuất dữ liệu từ requestData
			String jobName = (String) requestData.get("jobName");
			LocalDate deadline = LocalDate.parse((String) requestData.get("deadline"));
			List<Integer> executedUserIds = (List<Integer>) requestData.get("executedUserIds");

			// Chuyển danh sách từ Integer sang Long
			List<Long> executedUserIdsLong = executedUserIds.stream().map(Integer::longValue)
					.collect(Collectors.toList());

			// Gọi service để tạo công việc
			Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserIdsLong);

			return ResponseEntity.status(HttpStatus.CREATED).body(job);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/createApproveJob")
	public ResponseEntity<?> createApproveJob(@RequestParam String jobName, @RequestParam LocalDate deadline,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		try {
			Job job = jobService.createApproveJob(userId, jobName, deadline);
			return ResponseEntity.ok(job);
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

	@GetMapping("/executed/{id}")
	public ResponseEntity<?> getJobsByExecutedId(@PathVariable Long id) {
		try {
			List<Map<String, Object>> jobs = jobService.getJobsByExecutedId(id);
			return ResponseEntity.ok(jobs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
		}
	}

	@GetMapping("/sub-users")
	public ResponseEntity<List<User>> getSubUsers(HttpSession session) {
		// Lấy userId từ session
		Long userId = (Long) session.getAttribute("userId");

		// Kiểm tra nếu chưa đăng nhập
		if (userId == null) {
			return ResponseEntity.status(401).body(null);
		}

		// Gọi service để lấy danh sách user con
		List<User> subUsers = userService.getSubUsersByUserId(userId);
		return ResponseEntity.ok(subUsers);
	}

//
	@GetMapping("/subordinates-jobs")
	public ResponseEntity<?> getJobsOfSubordinates(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		try {
			List<Job> jobs = jobService.getJobsOfSubordinates(userId);
			return ResponseEntity.ok(jobs);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/update-status")
	public ResponseEntity<?> updateJobStatus(@RequestParam Long jobId, @RequestParam JobStatus newStatus,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		try {
			Job updatedJob = jobService.updateJobStatus(jobId, userId, newStatus);
			return ResponseEntity.ok(updatedJob);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/approveJob")
	public ResponseEntity<?> approveJob(@RequestParam Long jobId, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		try {
			Job job = jobService.approveJob(userId, jobId);
			return ResponseEntity.ok(job);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/rejectApproveJob")
	public ResponseEntity<?> rejectJob(@RequestParam Long jobId, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		try {
			Job job = jobService.rejectJob(userId, jobId);
			return ResponseEntity.ok(job);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/count-by-status")
	public ResponseEntity<Map<JobStatus, Long>> getJobStatusCount(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		Map<JobStatus, Long> jobStatusCount = jobService.countJobsByStatusForSubordinates(userId);
		return ResponseEntity.ok(jobStatusCount);
	}

//		@Autowired
//		private UserService userService;
//		@Autowired
//		private JobService jobService;
//		@Autowired
//		private DepartmentService departmentService;
//		@Autowired
//		private UserReponsitory userReponsitory;
//
//		@PostMapping("/login")
//	    public ResponseEntity<?> login(
//	            @RequestParam String userName,
//	            @RequestParam String password) {
//	        
//	        boolean isAuthenticated = userService.login(userName, password);
//	        
//	        if (isAuthenticated) {
//	            return ResponseEntity.ok("Login successful");
//	        } else {
//	            return ResponseEntity.status(401).body("Invalid credentials");
//	        }
//	    }
//
//
//		@GetMapping("/sub-department/{userId}")
//		public ResponseEntity<List<User>> getUsersInSubDepartments(@PathVariable Long userId) {
//			User user = userReponsitory.findById(userId)
//					.orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + userId));
//
//			// Lấy phòng ban của user
//			Department userDepartment = user.getDepartment();
//			if (userDepartment == null) {
//				return ResponseEntity.ok(Collections.emptyList());
//			}
//
//			// Gọi service để lấy danh sách user trong phòng ban con
//			List<User> users = departmentService.getUsersByDepartment(userDepartment.getDepartmentId());
//			return ResponseEntity.ok(users);
//		}
//
//		// tạo việc
//		@PostMapping("/createJob")
//		public ResponseEntity<?> createJob(@RequestParam String jobName, @RequestParam LocalDate deadline,
//				@RequestParam Long createdUserId, @RequestParam Long executedUserId) {
//			try {
//				Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserId);
//				return ResponseEntity.ok(job);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//
//		// tạo yêu cầu
//		@PostMapping("/createApproveJob")
//		public ResponseEntity<?> createJob(@RequestParam Long id, @RequestParam String jobName,
//				@RequestParam LocalDate deadline) {
//			try {
//				Job job = jobService.createApproveJob(id, jobName, deadline);
//				return ResponseEntity.ok(job);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//
//		// xem list job
//		@GetMapping("/my-jobs")
//		public ResponseEntity<?> getJobsByExecutedId(@RequestParam Long executedId) {
//			try {
//				List<Job> jobs = jobService.getJobsByExecutedId(executedId);
//				return ResponseEntity.ok(jobs);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//
//		// xem list job của nhân viên
//		@GetMapping("/subordinates-jobs")
//		public ResponseEntity<?> getJobsOfSubordinates(@RequestParam Long approverId) {
//			try {
//				List<Job> jobs = jobService.getJobsOfSubordinates(approverId);
//				return ResponseEntity.ok(jobs);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//
//		// hoàn thành job
//		@PutMapping("/update-status")
//		public ResponseEntity<?> updateJobStatus(@RequestParam Long jobId, @RequestParam Long userId,
//				@RequestParam JobStatus newStatus) {
//			try {
//				Job updatedJob = jobService.updateJobStatus(jobId, userId, newStatus);
//				return ResponseEntity.ok(updatedJob);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//
//		// duyệt yêu cầu job
//		@PutMapping("/approveJob")
//		public ResponseEntity<?> approveJob(@RequestParam Long approverId, @RequestParam Long jobId) {
//			try {
//				Job job = jobService.approveJob(approverId, jobId);
//				return ResponseEntity.ok(job);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//
//		// từ chối
//		@PutMapping("/rejectApproveJob")
//		public ResponseEntity<?> rejectJob(@RequestParam Long approverId, @RequestParam Long jobId) {
//			try {
//				Job job = jobService.rejectJob(approverId, jobId);
//				return ResponseEntity.ok(job);
//			} catch (Exception e) {
//				return ResponseEntity.badRequest().body(e.getMessage());
//			}
//		}
//		@GetMapping("/count-by-status")
//	    public ResponseEntity<Map<JobStatus, Long>> getJobStatusCount(@RequestParam Long approverId) {
//	        Map<JobStatus, Long> jobStatusCount = jobService.countJobsByStatusForSubordinates(approverId);
//	        return ResponseEntity.ok(jobStatusCount);
//	    }

}