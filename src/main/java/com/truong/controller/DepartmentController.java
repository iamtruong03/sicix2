package com.truong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.truong.service.DepartmentService;
import com.truong.service.UserService;

@RestController
@RequestMapping("/department")
public class DepartmentController {
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	
//	@GetMapping
//	public DepartmentEn
}
