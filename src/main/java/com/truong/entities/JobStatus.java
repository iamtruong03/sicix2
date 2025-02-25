package com.truong.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobStatus")
public class JobStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long jobStatusId;
	private String jobStatusName;
	public Long getJobStatusId() {
		return jobStatusId;
	}
	public void setJobStatusId(Long jobStatusId) {
		this.jobStatusId = jobStatusId;
	}
	public String getJobStatusName() {
		return jobStatusName;
	}
	public void setJobStatusName(String jobStatusName) {
		this.jobStatusName = jobStatusName;
	}
	
}
