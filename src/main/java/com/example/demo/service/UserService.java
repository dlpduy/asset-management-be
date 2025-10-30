package com.example.demo.service;

import com.example.demo.dto.user.UpdateUserRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.entity.Department;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.DataNotFound;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream().map(UserResponse::fromUser).toList();
    }

    public UserResponse updateUser(UpdateUserRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new DataNotFound("User not found"));

        user.setName(request.getName());
        user.setRole(request.getRole());

        if (user.getRole() == Role.ADMIN) {
            return UserResponse.fromUser(userRepository.save(user));
        }
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new DataNotFound("Department not found"));
        user.setDepartment(department);
        return UserResponse.fromUser(userRepository.save(user));
    }
}
