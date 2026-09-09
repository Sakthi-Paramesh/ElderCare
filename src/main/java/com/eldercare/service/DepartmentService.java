package com.eldercare.service;

import com.eldercare.dto.DepartmentRequest;
import com.eldercare.entity.Department;
import com.eldercare.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    @Transactional
    public Department createDepartment(DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department department = getDepartmentById(id);
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}
