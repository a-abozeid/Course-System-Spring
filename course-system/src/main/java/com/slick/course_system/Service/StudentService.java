package com.slick.course_system.Service;

import com.slick.course_system.Dto.StudentDto;

import java.util.List;

public interface StudentService {
    StudentDto createStudent(StudentDto studentDto);
    List<StudentDto> findAll();
    StudentDto findOne(int id);
    StudentDto updateStudent(StudentDto studentDto, int id);
    void deleteStudent(int id);

    StudentDto registerCourseToStudent(int studentId, int courseId);
    StudentDto cancelCourseRegistration(int studentId, int courseId);

    byte[] generateStudentSchedulePdf(int studentId);
}
