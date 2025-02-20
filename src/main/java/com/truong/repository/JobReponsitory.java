package com.truong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.truong.entities.Job;

public interface JobReponsitory extends JpaRepository<Job, Long>{

}
