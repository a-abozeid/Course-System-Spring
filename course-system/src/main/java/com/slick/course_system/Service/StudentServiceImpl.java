package com.slick.course_system.Service;

import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = studentDtoToStudent(studentDto);
        student.setPassword(bCryptPasswordEncoder.encode(student.getPassword()));
        return studentToStudentDto(studentRepository.save(student));
    }

    @Override
    @Cacheable(value = "students", key = "'allStudents'")
    public List<StudentDto> findAll() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map(student -> studentToStudentDto(student)).toList();
    }

    @Override
    @Cacheable(value = "students", key = "#id")
    public StudentDto findOne(int id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundExc("Student not found"));

        return studentToStudentDto(student);
    }

    @Override
    public StudentDto updateStudent(StudentDto studentDto, int id) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundExc("Student not found"));

        existingStudent.setName(studentDto.getName());
        existingStudent.setEmail(studentDto.getEmail());
        existingStudent.setPhoneNum(studentDto.getPhoneNum());
        existingStudent.setPassword(bCryptPasswordEncoder.encode(studentDto.getPassword()));

        studentRepository.save(existingStudent);
        return studentToStudentDto(existingStudent);
    }

    @Override
    @CacheEvict(value = "students", key = "#id")
    public void deleteStudent(int id) {
        studentRepository.findById(id).orElseThrow(() -> new NotFoundExc("Student not found"));
        studentRepository.deleteById(id);
    }

    @Override
    public StudentDto registerCourseToStudent(int studentId, int courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        if (!student.getCourses().contains(course)) {
            student.getCourses().add(course);
        }

        Student updatedStudent = studentRepository.save(student);
        return studentToStudentDto(updatedStudent);
    }

    @Override
    public StudentDto cancelCourseRegistration(int studentId, int courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        if (student.getCourses().contains(course)) {
            student.getCourses().remove(course);
        } else {
            throw new RuntimeException("Course not registered to the student");
        }

        Student updatedStudent = studentRepository.save(student);
        return studentToStudentDto(updatedStudent);
    }

    @Override
    public byte[] generateStudentSchedulePdf(int studentId) {
        StudentDto student = findOne(studentId); // Retrieve student info (uses @Cacheable)

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

        try {
            // Add title
            document.add(new Paragraph("Schedule for " + student.getName()).setBold().setFontSize(16));

            // Create a table for the schedule
            Table table = new Table(3);
            table.addCell("Course Name");
            table.addCell("Credit Hours");
            table.addCell("Time Slot");

            // Add courses to the table
            for (CourseDto course : student.getCourses()) {
                table.addCell(course.getName());
                table.addCell(course.getCreditHours());
                table.addCell(course.getTimeSlot());
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return baos.toByteArray(); // Return the PDF content as a byte array
    }

    //*********************************************************************************
    public StudentDto studentToStudentDto(Student student) {
        if (student == null) {
            return null;
        }

        StudentDto studentDto = new StudentDto();
        studentDto.setName(student.getName());
        studentDto.setEmail(student.getEmail());
        studentDto.setPhoneNum(student.getPhoneNum());
        studentDto.setPassword(student.getPassword());

        Set<CourseDto> courseDtos = new HashSet<>();
        for (Course course : student.getCourses()) {
            CourseDto courseDto = new CourseDto();
            courseDto.setName(course.getName());
            courseDto.setCreditHours(course.getCreditHours());
            courseDto.setTimeSlot(course.getTimeSlot());
            courseDtos.add(courseDto);
        }
        studentDto.setCourses(courseDtos);

        return studentDto;
    }

    public Student studentDtoToStudent(StudentDto studentDto) {
        if (studentDto == null) {
            return null;
        }

        Student student = new Student();
        student.setName(studentDto.getName());
        student.setEmail(studentDto.getEmail());
        student.setPhoneNum(studentDto.getPhoneNum());
        student.setPassword(studentDto.getPassword());

        Set<Course> courses = new HashSet<>();
        for (CourseDto courseDto : studentDto.getCourses()) {
            Course course = new Course();
            course.setName(courseDto.getName());
            course.setCreditHours(courseDto.getCreditHours());
            course.setTimeSlot(courseDto.getTimeSlot());
            courses.add(course);
        }
        student.setCourses(courses);

        return student;
    }

}
