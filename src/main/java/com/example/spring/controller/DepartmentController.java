package com.example.spring.controller;

import com.example.spring.entity.Department;
import com.example.spring.error.DepartmentNotFoundException;
import com.example.spring.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@RestController
public class DepartmentController {

    @Autowired
    private  DepartmentService departmentService;

    private final Logger logger =
            LoggerFactory.getLogger(DepartmentController.class);

    @PostMapping("/departments")
    public Department saveDepartment(@Valid @RequestBody Department department){
        return departmentService.saveDepartment(department);
    }

    @GetMapping("/departments")
    public List<Department> fetchDepartmentList(){
        LOGGER.info("Inside saveDepartment");
        return departmentService. fetchDepartmentList();
    }

    @GetMapping("/departments/{id}")
    public Department fetchDepartmentById(@PathVariable("id") Long departmentId) throws DepartmentNotFoundException {
        return departmentService.fetchDepartmentById(departmentId);
    }

    @DeleteMapping("/departments/{id}")
    public String deleteDepartmentById(@PathVariable("id") Long departmentId, @PathVariable String id){
        departmentService.deleteDepartmentById(departmentId);
        return "Department deleted successfully!!";
    }

    @PutMapping("/departments/{id}")
    public Department updateDepartment(@PathVariable("id") Long departmentId, @RequestBody Department department){
        return  departmentService.updateDepartment(departmentId,department);
    }

    @GetMapping("/departments/name/{name}")
    public Department fetchDepartmentByName( @PathVariable("name") String departmentName) {
        return departmentService.fetchDepartmentByName(departmentName);
    }
}
