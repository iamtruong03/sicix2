package com.truong.repository;

import com.truong.exception.JobStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.truong.entities.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobReponsitory extends JpaRepository<Job, Long>{
  @Query("SELECT j FROM Job j WHERE j.executedId.id = :executedId " +
      "AND (:status IS NULL OR j.status = :status)")
  List<Job> findJobsByExecutedIdAndStatus(@Param("executedId") Long executedId,
      @Param("status") JobStatus status);
}
