package com.slick.course_system.Dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
