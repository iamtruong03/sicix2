package com.truong.exception;

public enum JobStatus {
    PENDING_APPROVAL, // Đang chờ phê duyệt
    APPROVED,         // Đã được phê duyệt
    REJECTED,         // Bị từ chối
    IN_PROGRESS,      // Đang thực hiện
    COMPLETED,        // Đã hoàn thành
    OVERDUE          // Quá hạn
}
