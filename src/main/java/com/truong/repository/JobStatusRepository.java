package com.truong.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.truong.entities.JobStatus;

public interface JobStatusRepository extends JpaRepository<JobStatus, Long>{
	Optional<JobStatus> findByJobStatusName(String jobStatusName);

}
