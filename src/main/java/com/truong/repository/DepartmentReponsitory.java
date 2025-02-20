package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.truong.entities.Department;

public interface DepartmentReponsitory extends JpaRepository<Department, Long> {
	boolean existsByNameDepartment(String namedepartment);

	Optional<Department> findByNameDepartment(String namedepartment);

	@Query("SELECT d FROM Department d WHERE d.parentDepartment.departmentId = :parentId")
	List<Department> findSubDepartments(@Param("parentId") Long parentId);

	List<Department> findByParentDepartment(Department department);
	
//	 // Lấy danh sách phòng ban con từ parent_id
//    List<Department> findByParentId(Long parentId);
}
