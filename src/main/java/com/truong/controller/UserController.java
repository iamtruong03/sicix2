package com.truong.controller;

import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.repository.UserReponsitory;
import com.truong.service.DepartmentService;
import com.truong.service.JobService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
	public ResponseEntity<String> login(@RequestBody User user) {
		userService.login(user.getUsername(), user.getPassword());
		return ResponseEntity.ok("login successful");
	}

	@GetMapping("/sub-department/{userId}")
	public ResponseEntity<List<User>> getUsersInSubDepartments(@PathVariable Long userId) {
		User user = userReponsitory.findById(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + userId));

		// Lấy phòng ban của user
		Department userDepartment = user.getDepartment();
		if (userDepartment == null) {
			return ResponseEntity.ok(Collections.emptyList());
		}

		// Gọi service để lấy danh sách user trong phòng ban con
		List<User> users = departmentService.getUsersByDepartment(userDepartment.getDepartmentId());
		return ResponseEntity.ok(users);
	}

	@PostMapping("/createJob")
	public ResponseEntity<?> createJob(@RequestParam String jobName, @RequestParam LocalDate deadline,
			@RequestParam Long createdUserId, @RequestParam Long executedUserId) {
		try {
			Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserId);
			return ResponseEntity.ok(job);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/executed-jobs")
	public ResponseEntity<?> getJobsByExecutedId(@RequestParam Long approverId, @RequestParam Long executedId,
			@RequestParam(required = false) JobStatus status) {
		try {
			List<Job> jobs = jobService.getJobsByExecutedId(approverId, executedId, status);
			return ResponseEntity.ok(jobs);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PutMapping("/update-status")
	public ResponseEntity<?> updateJobStatus(@RequestParam Long jobId,
			@RequestParam Long userId,
			@RequestParam JobStatus newStatus) {
		try {
			Job updatedJob = jobService.updateJobStatus(jobId, userId, newStatus);
			return ResponseEntity.ok(updatedJob);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}