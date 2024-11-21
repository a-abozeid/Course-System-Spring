package com.slick.course_system.Service;

import com.slick.course_system.Dto.CourseDto;

import java.util.List;

public interface CourseService {
    CourseDto addCourse(CourseDto course);
    List<CourseDto> getAllCourses();
    CourseDto getCourseById(int id);
    CourseDto updateCourse(CourseDto course, int id);
    void deleteCourse(int id);
}
