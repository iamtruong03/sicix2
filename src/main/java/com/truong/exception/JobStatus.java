package com.truong.exception;

public enum JobStatus {
    WAITING, // Chờ duyệt
    REJECTED, // Từ chối
    IN_PROGRESS, // Đang thực hiện
    COMPLETED, // Hoàn thành
    OVERDUE // Quá hạn
}
