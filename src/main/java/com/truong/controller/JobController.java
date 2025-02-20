package com.truong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.truong.entities.*;

import com.truong.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {
	@Autowired
	private JobService jobService;
	
	@GetMapping
	public ResponseEntity<List<Job>> getAllJobs(){
		List<Job> jobs = jobService.getAllJobs();
		return new ResponseEntity<>(jobs, HttpStatus.OK);
	}
}
