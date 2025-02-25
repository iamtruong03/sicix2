package com.truong.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long jobId;

	private String jobName;

	@ManyToMany
	@JoinTable(name = "job_executors", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	@JsonIgnoreProperties({ "address", "fullName", "password", "department", "username" })
	private Set<User> executedUsers = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "approver_id")
	@JsonIgnoreProperties({ "address", "fullName", "password", "department", "username" })
	private User approverId;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private JobStatus jobStatus;

	private LocalDate deadline; // Hạn chót công việc

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Set<User> getExecutedUsers() {
		return executedUsers;
	}

	public void setExecutedUsers(Set<User> executedUsers) {
		this.executedUsers = executedUsers;
	}

	public User getApproverId() {
		return approverId;
	}

	public void setApproverId(User approverId) {
		this.approverId = approverId;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
	
}
