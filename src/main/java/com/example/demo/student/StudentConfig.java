package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository){
        return args -> {
                    Student van = new Student(
                            "Van",
                            "van@phuoc",
                            LocalDate.of(2000, Month.APRIL, 5)
                    );

                    Student phuoc = new Student(
                            "Phuoc",
                            "phuoc@van",
                        LocalDate.of(1999, Month.JANUARY, 16)

                    );
                    repository.saveAll(
                            List.of(van, phuoc)
                    );
        };
    }
}
