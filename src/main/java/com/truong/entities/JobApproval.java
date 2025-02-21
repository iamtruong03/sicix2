package com.truong.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.truong.exception.ApprovalStatus;

@Entity
@Table(name = "job_approval")
public class JobApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate approvalDate; // Ngày phê duyệt

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status; // Trạng thái phê duyệt

    @ManyToOne
    @JoinColumn(name = "accept_id", nullable = false) // Người phê duyệt
    private User acceptBy;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false) // Khóa ngoại liên kết với job
    private Job job;

    // Getters & Setters
}
