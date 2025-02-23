package com.truong.controller;

import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.repository.UserReponsitory;
import com.truong.service.DepartmentService;
import com.truong.service.JobService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.truong.exception.JobStatus;
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
	private UserReponsitory userReponsitory;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String userName, @RequestParam String password, HttpSession session) {
	    Long userId = userService.authenticate(userName, password);
	    if (userId != null) {
	        session.setAttribute("userId", userId);
	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Login successful");
	        response.put("userId", userId);
	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.status(401).body(Collections.singletonMap("message", "Invalid credentials"));
	    }
	}

	    @GetMapping("/sub-department")
	    public ResponseEntity<List<User>> getUsersInSubDepartments(HttpSession session) {
	        Long userId = (Long) session.getAttribute("userId");
	        User user = userReponsitory.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
	        Department userDepartment = user.getDepartment();
	        if (userDepartment == null) {
	            return ResponseEntity.ok(Collections.emptyList());
	        }
	        List<User> users = departmentService.getUsersByDepartment(userDepartment.getDepartmentId());
	        return ResponseEntity.ok(users);
	    }

	   @PostMapping("/createJob")
		public ResponseEntity<?> createJob(@RequestParam String jobName, 
		                                   @RequestParam LocalDate deadline,
		                                   @RequestParam Long executedUserId,
		                                   HttpSession session) {
		    try {
		        Long createdUserId = (Long) session.getAttribute("userId"); // Lấy userId từ session
		        if (createdUserId == null) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
		        }
		        
		        Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserId);
		        return ResponseEntity.ok(job);
		    } catch (Exception e) {
		        return ResponseEntity.badRequest().body(e.getMessage());
		    }
		}


	    @PostMapping("/createApproveJob")
	    public ResponseEntity<?> createApproveJob(@RequestParam String jobName, @RequestParam LocalDate deadline, HttpSession session) {
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
	        Long userId = (Long) session.getAttribute("userId");
	        try {
	            List<Job> jobs = jobService.getJobsByExecutedId(userId);
	            return ResponseEntity.ok(jobs);
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        }
	    }

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
	    public ResponseEntity<?> updateJobStatus(@RequestParam Long jobId, @RequestParam JobStatus newStatus, HttpSession session) {
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