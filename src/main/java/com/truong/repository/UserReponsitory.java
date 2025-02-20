package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.truong.entities.Department;
import com.truong.entities.User;

public interface UserReponsitory extends JpaRepository<User, Long> {
	boolean existsByUserName(String username);

	Optional<User> findByUserName(String username);

	List<User> findByDepartment(Department department);

	// Lấy danh sách user không có phòng ban
	List<User> findByDepartmentIsNull();

    // Lấy danh sách user theo departmentId (bao gồm phòng ban con)
    @Query("SELECT u FROM User u WHERE u.department.departmentId = :departmentId")
    List<User> findUsersByDepartmentId(Long departmentId);

    // Lấy danh sách user theo danh sách department_id
    @Query("SELECT u FROM User u WHERE u.department.id IN :departmentIds")
    List<User> findByDepartmentIds(List<Long> departmentIds);
}
