package com.truong.repository;

import com.truong.exception.JobStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.truong.entities.Job;
import com.truong.entities.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobReponsitory extends JpaRepository<Job, Long>{
//  @Query("SELECT j FROM Job j WHERE j.executedId.id = :executedId " +
//      "AND (:status IS NULL OR j.status = :status)")
//  List<Job> findJobsByExecutedIdAndStatus(@Param("executedId") Long executedId,
//      @Param("status") JobStatus status);
  List<Job> findByExecutedId(User executedId);
  @Query("SELECT j FROM Job j WHERE j.approverId = :approver OR j.executedId IN :subordinateUsers")
  List<Job> findJobsOfSubordinates(@Param("approver") User approver, @Param("subordinateUsers") List<User> subordinateUsers);
  @Query("SELECT j.status, COUNT(j) FROM Job j " +
	       "WHERE j.approverId = :approver OR j.executedId IN :subordinateUsers " +
	       "GROUP BY j.status")
	List<Object[]> countJobsByStatusForSubordinates(@Param("approver") User approver, 
	                                                @Param("subordinateUsers") List<User> subordinateUsers);

}
