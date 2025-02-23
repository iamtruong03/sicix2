package com.truong.service;

import com.truong.entities.Job;
import com.truong.entities.User;
import com.truong.exception.JobStatus;
import com.truong.repository.JobReponsitory;
import com.truong.repository.UserReponsitory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {
  @Autowired
  private UserReponsitory userReponsitory;
  @Autowired
  private JobReponsitory jobReponsitory;
  @Autowired
  private DepartmentService departmentService;

  public Job createJob(String jobName, LocalDate deadline, Long createdUserId, Long executedUserId) {
    if (createdUserId.equals(executedUserId)) {
      throw new RuntimeException("Người tạo không được trùng với người thực hiện!");
    }
    // Tìm thông tin user tạo
    User createdUser = userReponsitory.findById(createdUserId)
        .orElseThrow(() -> new RuntimeException("User tạo không tồn tại!"));
    // Lấy danh sách nhân viên thuộc phòng ban con của người tạo
    Long departmentId = createdUser.getDepartment().getDepartmentId();
    List<User> allowedExecutors = departmentService.getUsersByDepartment(departmentId);
    // Kiểm tra user thực hiện có hợp lệ không
    User executedUser = userReponsitory.findById(executedUserId)
        .orElseThrow(() -> new RuntimeException("User thực hiện không tồn tại!"));
    if (!allowedExecutors.contains(executedUser)) {
      throw new RuntimeException("Người thực hiện không thuộc danh sách phòng ban con của người tạo!");
    }

    // Tạo công việc mới
    Job job = new Job();
    job.setJobName(jobName);
    job.setDeadline(deadline);
    job.setStatus(JobStatus.IN_PROGRESS);
    job.setCreatedId(createdUser);
    job.setExecutedId(executedUser);
    job.setApproverId(createdUser); // Người tạo cũng là người phê duyệt

    return jobReponsitory.save(job);
  }

  public List<Job> getJobsByExecutedId(Long approverId, Long executedId, JobStatus status) {
    // Kiểm tra approverId có hợp lệ không
    User approver = userReponsitory.findById(approverId)
        .orElseThrow(() -> new RuntimeException("Người duyệt không tồn tại!"));

    // Kiểm tra executedId có hợp lệ không
    User executedUser = userReponsitory.findById(executedId)
        .orElseThrow(() -> new RuntimeException("Người thực hiện không tồn tại!"));

    // Lấy danh sách công việc
    return jobReponsitory.findJobsByExecutedIdAndStatus(executedId, status);
  }

  public Job updateJobStatus(Long jobId, Long userId, JobStatus newStatus) {
    // Lấy công việc từ DB
    Job job = jobReponsitory.findById(jobId)
        .orElseThrow(() -> new RuntimeException("Công việc không tồn tại!"));

    // Kiểm tra nếu job đã quá hạn deadline → tự động cập nhật thành OVERDUE
    if (LocalDate.now().isAfter(job.getDeadline())) {
      job.setStatus(JobStatus.OVERDUE);
      return jobReponsitory.save(job);
    }

    // Chỉ executedId (người thực hiện) mới có quyền cập nhật trạng thái
    if (!job.getExecutedId().getId().equals(userId)) {
      throw new RuntimeException("Bạn không có quyền cập nhật công việc này!");
    }

    // Chỉ cho phép cập nhật từ IN_PROGRESS → COMPLETED
    if (job.getStatus() == JobStatus.IN_PROGRESS && newStatus == JobStatus.COMPLETED) {
      job.setStatus(JobStatus.COMPLETED);
      return jobReponsitory.save(job);
    } else {
      throw new RuntimeException("Không thể cập nhật trạng thái này!");
    }
  }

}
