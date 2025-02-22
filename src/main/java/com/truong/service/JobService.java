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
    User createdUser = userReponsitory.findById(createdUserId)
        .orElseThrow(() -> new RuntimeException("User tạo không tồn tại!"));
    User executedUser = userReponsitory.findById(executedUserId)
        .orElseThrow(() -> new RuntimeException("User thực hiện không tồn tại!"));

    // Lấy danh sách nhân viên của phòng ban từ executedUser
    Long departmentId = executedUser.getDepartment().getDepartmentId();
    List<User> allowedExecutors = departmentService.getUsersByDepartment(departmentId);

    // Kiểm tra executedUser có thuộc phòng ban không
    if (!allowedExecutors.contains(executedUser)) {
      throw new RuntimeException("Người thực hiện không hợp lệ!");
    }

    Job job = new Job();
    job.setJobName(jobName);
    job.setDeadline(deadline);
    job.setStatus(JobStatus.IN_PROGRESS); // Set trạng thái
    job.setCreatedId(createdUser);
    job.setExecutedId(executedUser);
    job.setApproverId(createdUser); // Người tạo cũng là người phê duyệt

    return jobReponsitory.save(job);
  }

}
