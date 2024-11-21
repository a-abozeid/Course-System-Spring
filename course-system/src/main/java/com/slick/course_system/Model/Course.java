package com.slick.course_system.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 20, message = "Name must be between 2-50 characters long")
    String name;

    @NotBlank(message = "creditHours is mandatory")
    @Size(min = 1, max = 1, message = "creditHours must be between 1-4 hours long")
    String creditHours;

    @NotBlank(message = "Time slot is mandatory")
    @Size(min = 3, max = 50, message = "Time slot must be between 3-20 characters")
    private String timeSlot; // e.g., "Monday 10-12 AM"

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students;
}
