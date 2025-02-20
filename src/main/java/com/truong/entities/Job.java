package com.truong.entities;

import java.time.LocalDateTime;

import com.truong.exception.JobStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long jobId;

	private String jobName;

	// Người tạo
	@ManyToOne
	@JoinColumn(name = "created_id", referencedColumnName = "id")
	private User createdId;

	// Người thực hiện
	@ManyToOne
	@JoinColumn(name = "executed_id", referencedColumnName = "id")
	private User executedId;

	@Enumerated(EnumType.STRING)
	private JobStatus status; // Trạng thái công việc

	private LocalDateTime deadline; // Hạn chót công việc

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

	public User getCreatedId() {
		return createdId;
	}

	public void setCreatedId(User createdId) {
		this.createdId = createdId;
	}

	public User getExecutedId() {
		return executedId;
	}

	public void setExecutedId(User executedId) {
		this.executedId = executedId;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

}
