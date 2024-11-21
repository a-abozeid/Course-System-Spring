package com.slick.course_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CourseSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseSystemApplication.class, args);
	}

}
