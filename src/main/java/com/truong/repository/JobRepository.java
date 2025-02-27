package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.truong.dto.UserDTO;
import com.truong.entities.Job;
import com.truong.entities.JobStatus;
import com.truong.entities.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {
	List<Job> findByExecutedUsersContaining(User user);

	@Query("SELECT j FROM Job j LEFT JOIN j.executedUsers e "
			+ "WHERE j.approverId = :approver OR e IN :subordinateUsers")
	List<Job> findJobsOfSubordinates(@Param("approver") User approver,
			@Param("subordinateUsers") List<User> subordinateUsers);

	@Query("SELECT j.jobStatus, COUNT(DISTINCT j) " + "FROM Job j JOIN j.executedUsers u "
			+ "WHERE u.id IN :subordinateIds " + "GROUP BY j.jobStatus")
	List<Object[]> countJobsByStatusForSubordinates(@Param("subordinateIds") List<Long> subordinateIds);

	@Query("SELECT j.jobStatus, COUNT(DISTINCT j) FROM Job j JOIN j.executedUsers u WHERE u = :user GROUP BY j.jobStatus")
	List<Object[]> countJobByExecutedUserStatus(@Param("user") User user);

	@Query("SELECT COUNT(j) > 0 FROM Job j WHERE j.approverId.id = :id")
	boolean existsByApproverId(@Param("id") Long id);



//  @Query("SELECT j FROM Job j WHERE j.executedId.id = :executedId " +
//      "AND (:status IS NULL OR j.status = :status)")
//  List<Job> findJobsByExecutedIdAndStatus(@Param("executedId") Long executedId,
//      @Param("status") JobStatus status);

//  List<Job> findByExecutedId(User executedId);

//  @Query("SELECT j.status, COUNT(j) FROM Job j " +
//	       "WHERE j.approverId = :approver OR j.executedId IN :subordinateUsers " +
//	       "GROUP BY j.status")
//	List<Object[]> countJobsByStatusForSubordinates(@Param("approver") User approver, 
//	                                                @Param("subordinateUsers") List<User> subordinateUsers);

}
