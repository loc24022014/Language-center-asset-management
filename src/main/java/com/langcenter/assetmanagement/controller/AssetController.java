package com.langcenter.assetmanagement.controller;

import com.langcenter.assetmanagement.dto.asset.*;
import com.langcenter.assetmanagement.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Quản lý danh mục tài sản/kho")
@SecurityRequirement(name = "bearerAuth")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả tài sản")
    public ResponseEntity<List<AssetResponse>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin tài sản theo ID")
    public ResponseEntity<AssetResponse> getAssetById(@PathVariable Integer id) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Thêm mới tài sản", description = "Chỉ ADMIN hoặc MANAGER được phép")
    public ResponseEntity<AssetResponse> createAsset(@Valid @RequestBody AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.createAsset(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cập nhật tài sản", description = "Chỉ ADMIN hoặc MANAGER được phép")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable Integer id, @Valid @RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.updateAsset(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa tài sản", description = "Chỉ ADMIN được phép")
    public ResponseEntity<Void> deleteAsset(@PathVariable Integer id) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }
}
