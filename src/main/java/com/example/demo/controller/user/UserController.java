package com.example.demo.controller.user;

import com.example.demo.dto.ResponseObject;
import com.example.demo.dto.user.UpdateUserRequest;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                .message("Get all user successful")
                .data(userService.getAllUser())
                .build());
    }

    @PutMapping("")
    public ResponseEntity<ResponseObject> updateUser(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseObject.builder()
                .message("Update user successful")
                .data(userService.updateUser(request))
                .build());
    }
}
