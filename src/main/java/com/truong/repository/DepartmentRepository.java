package com.truong.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.truong.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentId(Long departmentId);

    List<Department> findSubDepartmentByDepartmentId(Long departmentId);
    List<Department> findByParentDepartment(Department parentDepartment);
    boolean existsByNameDepartment(String namedepartment);

    @Query("SELECT d FROM Department d WHERE d.parentDepartment.departmentId = :parentId")
    List<Department> findByParentId(@Param("parentId") Long parentId);


}


