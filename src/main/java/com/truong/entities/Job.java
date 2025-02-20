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

	// Người giao việc (assigner)
	@ManyToOne
	@JoinColumn(name = "assigner_id", referencedColumnName = "id")
	private User createdId;

	// Người nhận việc (recipient)
	@ManyToOne
	@JoinColumn(name = "recipient_id", referencedColumnName = "id")
	private User recipientId;

	@Enumerated(EnumType.STRING)
	private JobStatus status; // Trạng thái công việc

	private LocalDateTime deadline; // Hạn chót công việc
}
