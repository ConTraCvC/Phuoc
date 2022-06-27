package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentByEmail = studentRepository
                .findStudentByEmail(student.getEmail());
        if(studentByEmail.isPresent()){
            try {
                throw new IllegalAccessException("email taken");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (!exists){
            try {
                throw new IllegalAccessException(
                        "student with id" + studentId + "does not exists");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId,
                              String name,
                              String email) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new IllegalArgumentException(
                        "student with id " + studentId + " does not exist"));

            if (name != null &&
                    name.length() > 0 &&
                    !Objects.equals(student.getName(), name)) {
                student.setName(name);
            }

            if (email != null &&
                    email.length() > 0 &&
                    !Objects.equals(student.getEmail(), email)) {
                Optional<Student> studentOptional = studentRepository
                        .findStudentByEmail(email);
                if (studentOptional.isPresent()) {
                    throw new IllegalArgumentException("email taken");
                }
                student.setEmail(email);
            }
    }
}
