package com.truong.controller;

import com.truong.dto.UserDTO;
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
	private UserReponsitory userReponsitory;

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
			HttpSession session) {
		String userName = loginRequest.get("userName");
		String password = loginRequest.get("password");

		if (userName == null || password == null) {
			throw new AppException(ErrorCode.INVALID_USER);
		}

		User user = userReponsitory.findByUserName(userName)
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
	        List<Long> executedUserIdsLong = executedUserIds.stream()
	            .map(Integer::longValue)
	            .collect(Collectors.toList());

	        Job job = jobService.createJob(jobName, deadline, createdUserId, executedUserIdsLong);

	        // Trả về JSON đúng định dạng mà FE mong đợi
	        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
	            "success", true,
	            "message", "Tạo công việc thành công!",
	            "job", job
	        ));
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(Map.of(
	            "success", false,
	            "message", e.getMessage()
	        ));
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

	@GetMapping("/sub-users")
	public ResponseEntity<List<UserDTO>> getSubUsers(HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		List<UserDTO> subUsers = userService.getSubUsersByUserId(userId);
		return ResponseEntity.ok(subUsers);
	}
	
	@GetMapping("/subordinate-statistics")
	public ResponseEntity<Map<String, Long>> getSubordinateJobStatistics(@SessionAttribute("userId") Long userId) {
	    Map<String, Long> statistics = jobService.countJobsByStatusForSubordinates(userId);
	    return ResponseEntity.ok(statistics);
	}



}
