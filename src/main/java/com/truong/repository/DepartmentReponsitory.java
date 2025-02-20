package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.truong.entities.Department;

@Repository
public interface DepartmentReponsitory extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentId(Long departmentId);
    List<Department> findByParentDepartment(Department parentDepartment);
    boolean existsByNameDepartment(String namedepartment);
}


