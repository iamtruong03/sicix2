package com.truong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.truong.entities.Department;
import com.truong.entities.User;
import com.truong.service.DepartmentService;
import com.truong.service.UserService;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private UserService userService;
	
//	@GetMapping
//	public DepartmentEn
	
	@GetMapping("/{departmentId}/users")
	public ResponseEntity<List<User>> getUsersByDepartment(@PathVariable Long departmentId) {
	    List<User> users = departmentService.getUsersByDepartment(departmentId);
	    return ResponseEntity.ok(users);
	}

	
	 // API lấy phòng ban cha
    @GetMapping("/{departmentId}/parent")
    public ResponseEntity<Department> getParent(@PathVariable Long departmentId) {
        Department parent = departmentService.getParent(departmentId);
        return parent != null ? ResponseEntity.ok(parent) : ResponseEntity.notFound().build();
    }

    // API lấy danh sách con của department_id
    @GetMapping("/{departmentId}/children")
    public ResponseEntity<List<Department>> getChildren(@PathVariable Long departmentId) {
        List<Department> children = departmentService.getChildren(departmentId);
        return ResponseEntity.ok(children);
    }

    // API lấy danh sách con cháu của department_id (tất cả cấp)
    @GetMapping("/{departmentId}/descendants")
    public ResponseEntity<List<Department>> getDescendants(@PathVariable Long departmentId) {
        List<Department> descendants = departmentService.getDescendants(departmentId);
        return ResponseEntity.ok(descendants);
    }
}
