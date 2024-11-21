package com.slick.course_system.Controller;

import com.slick.course_system.Dto.StudentDto;
import com.slick.course_system.Service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        return new ResponseEntity<>(studentService.createStudent(studentDto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable int id) {
        StudentDto student = studentService.findOne(id);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable int id, @Valid @RequestBody StudentDto studentDto) {
        StudentDto updatedStudent = studentService.updateStudent(studentDto, id);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>("Student deleted!", HttpStatus.OK);
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<StudentDto> registerCourse(
            @PathVariable int studentId,
            @PathVariable int courseId) {
        StudentDto updatedStudent = studentService.registerCourseToStudent(studentId, courseId);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<StudentDto> cancelCourseRegistration(
            @PathVariable int studentId,
            @PathVariable int courseId) {
        StudentDto updatedStudent = studentService.cancelCourseRegistration(studentId, courseId);
        return ResponseEntity.ok(updatedStudent);
    }

    @GetMapping("/students/{id}/schedule-pdf")
    public ResponseEntity<byte[]> downloadSchedulePdf(@PathVariable int id) {
        byte[] pdfData = studentService.generateStudentSchedulePdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_schedule.pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(pdfData);
    }
}
