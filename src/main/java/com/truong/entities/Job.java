package com.truong.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.truong.exception.JobStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long jobId;

  private String jobName;

  @ManyToOne
  @JoinColumn(name = "created_id")
  @JsonIgnoreProperties({"address", "fullName", "password", "department", "username"})
  private User createdId;

  @ManyToOne
  @JoinColumn(name = "executed_id")
  @JsonIgnoreProperties({"address", "fullName", "password", "department", "username"})
  private User executedId;

  @ManyToOne
  @JoinColumn(name = "approver_id")
  @JsonIgnoreProperties({"address", "fullName", "password", "department", "username"})
  private User approverId;

  @Enumerated(EnumType.STRING)
  private JobStatus status; // Trạng thái công việc

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

  public User getCreatedId() {
    return createdId;
  }

  public void setCreatedId(User createdId) {
    this.createdId = createdId;
  }

  public User getExecutedId() {
    return executedId;
  }


  public User getApproverId() {
    return approverId;
  }

  public void setApproverId(User approverId) {
    this.approverId = approverId;
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

  public LocalDate getDeadline() {
    return deadline;
  }

  public void setDeadline(LocalDate deadline) {
    this.deadline = deadline;
  }

}
