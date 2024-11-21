package com.slick.course_system.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto implements Serializable {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2-50 characters long")
    String name;

    @NotBlank(message = "creditHours is mandatory")
    @Size(min = 1, max = 1, message = "creditHours must be between 1-4 hours long")
    String creditHours;

    @NotBlank(message = "Time slot is mandatory")
    @Size(min = 3, max = 50, message = "Time slot must be between 3-20 characters")
    private String timeSlot;

    private Set<StudentDto> students;
}
