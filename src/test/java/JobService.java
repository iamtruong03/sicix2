
import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.entities.JobStatus;
import com.truong.entities.User;
import com.truong.repository.DepartmentReponsitory;
import com.truong.repository.JobReponsitory;
import com.truong.repository.JobStatusRepository;
import com.truong.repository.UserReponsitory;
import com.truong.service.DepartmentService;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobService {
	@Autowired
	private UserReponsitory userReponsitory;
	@Autowired
	private JobReponsitory jobReponsitory;
	@Autowired
	private JobStatusRepository jobStatusRepository;
	@Autowired
	private DepartmentReponsitory departmentReponsitory;
	@Autowired
	private DepartmentService departmentService;
	private final Random random = new Random();

	// tạo việc
	public Job createJob(String jobName, LocalDate deadline, Long createdUserId, List<Long> executedUserIds) {
		// Kiểm tra danh sách người thực hiện có trống không
		if (executedUserIds == null || executedUserIds.isEmpty()) {
			throw new RuntimeException("Phải có ít nhất một người thực hiện!");
		}

		// Tìm thông tin user tạo (cũng là người phê duyệt)
		User createdUser = userReponsitory.findById(createdUserId)
				.orElseThrow(() -> new RuntimeException("User tạo không tồn tại!"));

		// Lấy danh sách nhân viên thuộc phòng ban con của người tạo
		Long departmentId = createdUser.getDepartment().getDepartmentId();
		List<User> allowedExecutors = departmentService.getUsersByDepartment(departmentId);

		// Kiểm tra từng user thực hiện có hợp lệ không
		Set<User> executedUsers = new HashSet<>();
		for (Long executedUserId : executedUserIds) {
			if (createdUserId.equals(executedUserId)) {
				throw new RuntimeException("Người tạo không được trùng với người thực hiện!");
			}
			User executedUser = userReponsitory.findById(executedUserId)
					.orElseThrow(() -> new RuntimeException("User thực hiện không tồn tại!"));

			if (!allowedExecutors.contains(executedUser)) {
				throw new RuntimeException("Người thực hiện không thuộc danh sách phòng ban con của người tạo!");
			}

			executedUsers.add(executedUser);
		}

		// Lấy trạng thái mặc định của công việc
		JobStatus defaultStatus = jobStatusRepository.findByJobStatusName("IN_PROGRESS")
				.orElseThrow(() -> new RuntimeException("Trạng thái mặc định không tồn tại!"));

		// Tạo công việc mới
		Job job = new Job();
		job.setJobName(jobName);
		job.setDeadline(deadline);
		job.setJobStatus(defaultStatus); // Gán trạng thái từ bảng JobStatus
		job.setApproverId(createdUser); // Người tạo cũng là người phê duyệt
		job.setExecutedUsers(executedUsers);

		return jobReponsitory.save(job);
	}

	// xem list my job
	public List<Map<String, Object>> getJobsByExecutedId(Long id) {
		// Kiểm tra xem người dùng có tồn tại không
		User executedUser = userReponsitory.findById(id)
				.orElseThrow(() -> new RuntimeException("Người thực hiện không tồn tại!"));

		// Lấy danh sách công việc mà user đang thực hiện
		List<Job> jobs = jobReponsitory.findByExecutedUsersContaining(executedUser);

		// Chuyển đổi danh sách công việc sang danh sách Map chứa thông tin bổ sung
		return jobs.stream().map(job -> {
			Map<String, Object> jobInfo = new HashMap<>();
			jobInfo.put("id", job.getJobId());
			jobInfo.put("name", job.getJobName());

			// Lấy jobStatusName từ JobStatus
			jobInfo.put("jobStatusName", job.getJobStatus() != null ? job.getJobStatus().getJobStatusName() : null);

			// Lấy username của người duyệt (approver)
			jobInfo.put("approverUsername", job.getApproverId() != null ? job.getApproverId().getFullName() : null);

			// Lấy deadline của công việc
			jobInfo.put("deadline", job.getDeadline() != null ? job.getDeadline().toString() : null);

			return jobInfo;
		}).collect(Collectors.toList());
	}

//
	// xem list job của các phòng ban con	
	public List<Job> getJobsOfSubordinates(Long userId) {
	    // Tìm người duyệt theo userId
	    User approver = userReponsitory.findById(userId)
	            .orElseThrow(() -> new RuntimeException("Người duyệt không tồn tại!"));

	    // Lấy danh sách nhân viên cấp dưới của phòng ban con
	    List<User> subordinates = departmentService.getUsersByDepartment(approver.getDepartment().getDepartmentId());

	    // Nếu không có nhân viên cấp dưới, trả về danh sách rỗng
	    if (subordinates.isEmpty()) {
	        return List.of();
	    }

	    // Lấy danh sách công việc của cấp dưới
	    return jobReponsitory.findJobsOfSubordinates(approver, subordinates);
	}
//
//
	// complete Job
	public Job updateJobStatus(Long jobId, Long userId, JobStatus newStatus) {
		// Lấy công việc từ DB
		Job job = jobReponsitory.findById(jobId).orElseThrow(() -> new RuntimeException("Công việc không tồn tại!"));

		if (job.getStatus() == JobStatus.REJECTED) {
			throw new RuntimeException("Job đã bị từ chối, không thể chỉnh sửa!");
		}

		// Kiểm tra trạng thái có đúng không
		if (job.getStatus() != JobStatus.IN_PROGRESS) {
			throw new RuntimeException("Job chưa ở trạng thái IN_PROGRESS!");
		}

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

	// tạo yêu cầu
	@Transactional
	public Job createApproveJob(Long id, String jobName, LocalDate deadline) {
		// Lấy thông tin user tạo job
		User user = userReponsitory.findById(id).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

		// Lấy phòng ban của user
		Department userDepartment = user.getDepartment();
		if (userDepartment == null || userDepartment.getParentDepartment() == null) {
			throw new RuntimeException("Không tìm thấy phòng ban cấp trên!");
		}

		// Tìm phòng ban cha (parent department)
		Department parentDepartment = userDepartment.getParentDepartment();

		// Lấy danh sách user thuộc phòng ban cha
		List<User> approvers = userReponsitory.findByDepartment(parentDepartment);
		if (approvers.isEmpty()) {
			throw new RuntimeException("Không có người duyệt trong phòng ban cấp trên!");
		}

		// Chọn ngẫu nhiên một người duyệt
		User approver = approvers.get(random.nextInt(approvers.size()));

		// Tạo job mới
		Job job = new Job();
		job.setJobName(jobName);
		job.setCreatedId(user);
		job.setExecutedId(user);
		job.setApproverId(approver);
		job.setDeadline(deadline);
		job.setStatus(JobStatus.WAITING);

		return jobReponsitory.save(job);
	}

	// duyệt approve job
	@Transactional
	public Job approveJob(Long approverId, Long jobId) {
		Job job = jobReponsitory.findById(jobId).orElseThrow(() -> new RuntimeException("Job không tồn tại!"));

		// Kiểm tra người duyệt có đúng không
		if (!job.getApproverId().getId().equals(approverId)) {
			throw new RuntimeException("Bạn không có quyền duyệt job này!");
		}
		if (job.getStatus() == JobStatus.REJECTED) {
			throw new RuntimeException("Job đã bị từ chối, không thể chỉnh sửa!");
		}

		// Cập nhật trạng thái
		job.setStatus(JobStatus.IN_PROGRESS);
		return jobReponsitory.save(job);
	}

	// từ chối duyệt
	@Transactional
	public Job rejectJob(Long approverId, Long jobId) {
		Job job = jobReponsitory.findById(jobId).orElseThrow(() -> new RuntimeException("Job không tồn tại!"));

		if (!job.getApproverId().getId().equals(approverId)) {
			throw new RuntimeException("Bạn không có quyền từ chối job này!");
		}

		job.setStatus(JobStatus.REJECTED);
		return jobReponsitory.save(job);
	}
//	
	// thống kê
	public Map<JobStatus, Long> countJobsByStatusForSubordinates(Long approverId) {
        // Lấy thông tin của approver (người duyệt)
        User approver = userReponsitory.findById(approverId)
                .orElseThrow(() -> new RuntimeException("Người duyệt không tồn tại!"));

        // Lấy danh sách cấp dưới
        List<User> subordinates = departmentService.getUsersByDepartment(approver.getDepartment().getDepartmentId());

        // Gọi repository để lấy số lượng Job theo trạng thái
        List<Object[]> result = jobReponsitory.countJobsByStatusForSubordinates(approver, subordinates);

        // Chuyển đổi danh sách Object[] thành Map<JobStatus, Long>
        Map<JobStatus, Long> jobCountMap = new HashMap<>();
        for (Object[] row : result) {
            JobStatus status = (JobStatus) row[0];
            Long count = (Long) row[1];
            jobCountMap.put(status, count);
        }

        return jobCountMap;
    }

}
