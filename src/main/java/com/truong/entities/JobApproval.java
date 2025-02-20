package com.truong.entities;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

import com.truong.exception.ApprovalStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "job_approval")
public class JobApproval {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "job_id")
	private Job job;

	@ManyToOne
	@JoinColumn(name = "approver_id")
	private User approver;

	@Enumerated(EnumType.STRING)
	private ApprovalStatus status;

	private LocalDateTime approvalDate;
}
