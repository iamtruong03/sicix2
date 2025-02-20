package com.truong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.truong.repository.DepartmentReponsitory;
import com.truong.repository.UserReponsitory;

@Service
public class PermissionService {
	@Autowired
	private UserReponsitory userReponsitory;
	@Autowired
	private DepartmentReponsitory departmentReponsitory;
}
