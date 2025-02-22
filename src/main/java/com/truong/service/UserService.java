package com.truong.service;

import com.truong.exception.JobStatus;
import com.truong.repository.JobReponsitory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.entities.Department;
import com.truong.entities.Job;
import com.truong.entities.User;
import com.truong.exception.AppException;
import com.truong.exception.ErrorCode;
import com.truong.repository.DepartmentReponsitory;
import com.truong.repository.UserReponsitory;

@Service
public class UserService {
	@Autowired
	private UserReponsitory userReponsitory;
	@Autowired
	private JobReponsitory jobReponsitory;
	@Autowired
	private DepartmentReponsitory departmentReponsitory;

	public Boolean login(String userName, String password) {
		if(userName == null) {
			throw new AppException(ErrorCode.INVALID_USER);
		}
		if (password == null || password.isEmpty()) {
			throw new AppException(ErrorCode.INVALID_PASSWORD);
		}
		
		User user = userReponsitory.findByUserName(userName)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		return true;
	}


	// danh sach user duoi cap co the nhan job
//	public List<User> getAllowedExecutors() {
//		List<String> listSubDepartmentId = departmentReponsitory.findSubDepartmentByDepartmentId(this.departmentId);
//		List<User> users = user
//	}

}
