package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.truong.entities.Department;
import com.truong.entities.User;

public interface UserReponsitory extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String username);

	boolean existsByUserName(String username);

//	Optional<User> findByUserName(String username);
//
//	List<User> findByDepartment(Department department);
//
//	// Lấy danh sách user không có phòng ban
//	List<User> findByDepartmentIsNull();
}
