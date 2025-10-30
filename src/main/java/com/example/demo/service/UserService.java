package com.example.demo.service;

import com.example.demo.dto.user.UserResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream().map(UserResponse::fromUser).toList();
    }
}
