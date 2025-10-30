package com.example.demo.dto.user;

import com.example.demo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String name;
    private Long id;
    private Role role;
    private Long departmentId;
}
