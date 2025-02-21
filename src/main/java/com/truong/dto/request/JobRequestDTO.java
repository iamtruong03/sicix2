package com.truong.dto.request;

import java.time.LocalDate;

public class JobRequestDTO {
	private String jobName;
	private Long createdId;
	private Long executedId;
	private String status;
	private LocalDate deadline;

	// Getter và Setter
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Long getCreatedId() {
		return createdId;
	}

	public void setCreatedId(Long createdId) {
		this.createdId = createdId;
	}

	public Long getExecutedId() {
		return executedId;
	}

	public void setExecutedId(Long executedId) {
		this.executedId = executedId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
}