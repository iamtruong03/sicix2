package com.truong.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_executors")
@IdClass(JobExecutorsId.class) 
public class JobExecutors {

    @Id
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

