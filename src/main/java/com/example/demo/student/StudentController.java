package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class StudentController {


    private final StudentService studentService;

    @Autowired
    private StudentRepository eRepo;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping({"/showStudent", "/", "/list"})
    public ModelAndView showStudent(){
        ModelAndView mav =new ModelAndView("list-student");
        List<Student> list = eRepo.findAll();
        mav.addObject("student", list);
        return mav;
    }

    @GetMapping("/addStudentForm")
    public ModelAndView addStudentForm(){
        ModelAndView mav = new ModelAndView("add-student-form");
        Student newStudent = new Student();
        mav.addObject("student", newStudent);
        return mav;
    }

    @PostMapping("/saveStudent")
    public ModelAndView saveStudent(@ModelAttribute Student student) {
        eRepo.save(student);
        return showStudent();
    }

    @GetMapping ("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long studentId){
        ModelAndView mav = new ModelAndView("add-student-form");
        Student student = eRepo.findById(studentId).get();
        mav.addObject("student", student);
        return mav;
    }

    @GetMapping("/deleteStudent")
    public ModelAndView deleteStudent(@RequestParam Long studentId) {
        eRepo.deleteById(studentId);
        return showStudent();
    }
}
