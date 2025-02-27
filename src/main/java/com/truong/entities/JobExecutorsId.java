package com.truong.entities;

import java.io.Serializable;
import java.util.Objects;

public class JobExecutorsId implements Serializable {
	private Long job;
	private Long user;

	// Constructors, equals, hashCode
	public JobExecutorsId() {
	}

	public JobExecutorsId(Long job, Long user) {
		this.job = job;
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JobExecutorsId that = (JobExecutorsId) o;
		return Objects.equals(job, that.job) && Objects.equals(user, that.user);
	}

	@Override
	public int hashCode() {
		return Objects.hash(job, user);
	}
}
