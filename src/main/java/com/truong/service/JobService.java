package com.truong.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.entities.Job;
import com.truong.repository.JobReponsitory;

@Service
public class JobService {
	@Autowired
	private JobReponsitory jobReponsitory;
	
	public Job createJob(Job job) {
		return jobReponsitory.save(job);
	}
	
	public List<Job> getAllJobs(){
		return jobReponsitory.findAll();
		}
	
	
}
