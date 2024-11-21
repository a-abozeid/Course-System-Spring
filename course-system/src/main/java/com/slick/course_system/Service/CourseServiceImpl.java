package com.slick.course_system.Service;

import com.slick.course_system.Dto.CourseDto;
import com.slick.course_system.Dto.StudentDto;
import com.slick.course_system.Exception.NotFoundExc;
import com.slick.course_system.Model.Course;
import com.slick.course_system.Model.Student;
import com.slick.course_system.Repository.CourseRepository;
import com.slick.course_system.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public CourseDto addCourse(CourseDto course) {
        return courseToCourseDto(courseRepository.save(courseDtoToCourse(course)));
    }

    @Override
    @Cacheable(value = "courses", key = "'allcourses'")
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream().map(course -> courseToCourseDto(course)).toList();
    }

    @Override
    @Cacheable(value = "courses", key = "#id")
    public CourseDto getCourseById(int id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundExc("Course not found"));
        return courseToCourseDto(course);
    }

    @Override
    public CourseDto updateCourse(CourseDto courseDto, int id) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundExc("Course not found"));

        existingCourse.setName(courseDto.getName());
        existingCourse.setCreditHours(courseDto.getCreditHours());
        existingCourse.setTimeSlot(courseDto.getTimeSlot());
        Course savedCourse = courseRepository.save(existingCourse);
        return courseToCourseDto(savedCourse);
    }

    @Override
    @CacheEvict(value = "courses", key = "#id")
    public void deleteCourse(int id) {
        courseRepository.findById(id).orElseThrow(() -> new NotFoundExc("Course not found"));
        courseRepository.deleteById(id);
    }

    //*********************************************************************************
    public CourseDto courseToCourseDto(Course course) {
        if (course == null) {
            return null;
        }

        CourseDto courseDto = new CourseDto();
        courseDto.setName(course.getName());
        courseDto.setCreditHours(course.getCreditHours());
        courseDto.setTimeSlot(course.getTimeSlot());

        Set<StudentDto> studentDtos = new HashSet<>();
        for (Student student : course.getStudents()) {
            StudentDto studentDto = new StudentDto();
            studentDto.setName(student.getName());
            studentDto.setEmail(student.getEmail());
            studentDto.setPhoneNum(student.getPhoneNum());
            studentDto.setPassword(student.getPassword());
            studentDtos.add(studentDto);
        }
        courseDto.setStudents(studentDtos);

        return courseDto;
    }

    public Course courseDtoToCourse(CourseDto courseDto) {
        if (courseDto == null) {
            return null;
        }

        Course course = new Course();
        course.setName(courseDto.getName());
        course.setCreditHours(courseDto.getCreditHours());
        course.setTimeSlot(courseDto.getTimeSlot());

        Set<Student> students = new HashSet<>();
        for (StudentDto studentDto : courseDto.getStudents()) {
            Student student = new Student();
            student.setName(studentDto.getName());
            student.setEmail(studentDto.getEmail());
            student.setPhoneNum(studentDto.getPhoneNum());
            student.setPassword(studentDto.getPassword());
            students.add(student);
        }
        course.setStudents(students);

        return course;
    }

}
