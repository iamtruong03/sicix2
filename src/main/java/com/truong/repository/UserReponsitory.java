package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.truong.entities.Department;
import com.truong.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserReponsitory extends JpaRepository<User, Long> {

//	Optional<User> findByUsernameAndPassword(String userName, String password);

	Optional<User> findByUserName(String username);

	@Query("SELECT u FROM User u WHERE u.department.departmentId IN :departmentIds")
	List<User> findByDepartmentIds(@Param("departmentIds") List<Long> departmentIds);

	List<User> findByDepartment(Department department);
//  boolean existsByUserName(String username);
//  List<User> findByDepartmentId(List<Long> departmentId);
//  @Query("SELECT u FROM User u WHERE u.department.departmentId = :departmentId")
//  List<User> findByDepartmentId(@Param("departmentId") Long departmentId);

}
