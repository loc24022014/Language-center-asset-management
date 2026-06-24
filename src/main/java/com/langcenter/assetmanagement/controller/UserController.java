package com.langcenter.assetmanagement.controller;

import com.langcenter.assetmanagement.dto.user.CreateUserRequest;
import com.langcenter.assetmanagement.dto.user.UserResponse;
import com.langcenter.assetmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Quản lý tài khoản người dùng")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách tất cả tài khoản")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo tài khoản mới")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Khóa hoặc Mở khóa tài khoản")
    public ResponseEntity<UserResponse> toggleActive(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.toggleActive(id));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật quyền (Role) cho tài khoản")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Integer id, @RequestParam Integer roleId) {
        return ResponseEntity.ok(userService.updateUserRole(id, roleId));
    }
}
