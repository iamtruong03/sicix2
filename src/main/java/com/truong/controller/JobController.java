package com.truong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.truong.dto.request.JobRequestDTO;
import com.truong.entities.*;
import com.truong.exception.JobStatus;
import com.truong.repository.JobReponsitory;
import com.truong.repository.UserReponsitory;
import com.truong.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {
	@Autowired
	private JobService jobService;
	@Autowired
	private JobReponsitory jobReponsitory;
	@Autowired
	private UserReponsitory userReponsitory;
	
	@GetMapping
	public ResponseEntity<List<Job>> getAllJobs(){
		List<Job> jobs = jobService.getAllJobs();
		return new ResponseEntity<>(jobs, HttpStatus.OK);
	}
	
//	@GetMapping
//	public List<JobDTO> getAllJobs() {
//	    List<Job> jobs = jobReponsitory.findAll();
//	    return jobs.stream().map(JobDTO::new).collect(Collectors.toList());
//	}

	
	@PostMapping
	public ResponseEntity<Job> createJobs(@RequestBody JobRequestDTO jobDTO) {
	    // Kiểm tra và lấy User từ database
	    User createdUser = userReponsitory.findById(jobDTO.getCreatedId())
	                          .orElseThrow(() -> new RuntimeException("Người tạo không tồn tại"));
	    User executedUser = userReponsitory.findById(jobDTO.getExecutedId())
	                          .orElseThrow(() -> new RuntimeException("Người thực hiện không tồn tại"));

	    // Chuyển đổi từ DTO sang Entity
	    Job job = new Job();
	    job.setJobName(jobDTO.getJobName());
	    job.setCreatedId(createdUser);
	    job.setExecutedId(executedUser);
	    job.setStatus(JobStatus.valueOf(jobDTO.getStatus())); // Chuyển đổi từ String sang Enum
	    job.setDeadline(jobDTO.getDeadline());

	    // Lưu vào database
	    Job createdJob = jobService.createJob(job);
	    return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
	}



}
