package com.langcenter.assetmanagement.controller;

import com.langcenter.assetmanagement.dto.auth.*;
import com.langcenter.assetmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Đăng nhập và lấy JWT token")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Trả về JWT access token khi thông tin hợp lệ")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
