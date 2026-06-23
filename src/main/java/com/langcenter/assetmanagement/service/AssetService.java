package com.langcenter.assetmanagement.service;

import com.langcenter.assetmanagement.dto.asset.*;
import com.langcenter.assetmanagement.entity.*;
import com.langcenter.assetmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssetResponse createAsset(AssetRequest request) {
        if (assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new IllegalArgumentException("Mã tài sản đã tồn tại: " + request.getAssetCode());
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        Asset asset = Asset.builder()
                .assetCode(request.getAssetCode())
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .totalQuantity(request.getTotalQuantity())
                .availableQuantity(request.getTotalQuantity()) // Khởi tạo bằng total
                .unit(request.getUnit())
                .location(request.getLocation())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .createdBy(currentUser)
                .build();

        Asset saved = assetRepository.save(asset);
        return toResponse(saved);
    }

    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AssetResponse getAssetById(Integer id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy tài sản ID: " + id));
        return toResponse(asset);
    }

    @Transactional
    public AssetResponse updateAsset(Integer id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy tài sản ID: " + id));

        if (!asset.getAssetCode().equals(request.getAssetCode()) &&
                assetRepository.existsByAssetCode(request.getAssetCode())) {
            throw new IllegalArgumentException("Mã tài sản đã tồn tại: " + request.getAssetCode());
        }

        // Nếu thay đổi total quantity, phải cập nhật lại available quantity tương ứng
        int diff = request.getTotalQuantity() - asset.getTotalQuantity();
        int newAvailable = asset.getAvailableQuantity() + diff;
        if (newAvailable < 0) {
            throw new IllegalArgumentException("Không thể giảm tổng số lượng vì số lượng khả dụng sẽ bị âm");
        }

        asset.setAssetCode(request.getAssetCode());
        asset.setName(request.getName());
        asset.setCategory(request.getCategory());
        asset.setDescription(request.getDescription());
        asset.setTotalQuantity(request.getTotalQuantity());
        asset.setAvailableQuantity(newAvailable);
        asset.setUnit(request.getUnit());
        asset.setLocation(request.getLocation());
        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }

        return toResponse(assetRepository.save(asset));
    }

    @Transactional
    public void deleteAsset(Integer id) {
        if (!assetRepository.existsById(id)) {
            throw new NoSuchElementException("Không tìm thấy tài sản ID: " + id);
        }
        assetRepository.deleteById(id);
    }

    private AssetResponse toResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetCode(asset.getAssetCode())
                .name(asset.getName())
                .category(asset.getCategory())
                .description(asset.getDescription())
                .totalQuantity(asset.getTotalQuantity())
                .availableQuantity(asset.getAvailableQuantity())
                .unit(asset.getUnit())
                .location(asset.getLocation())
                .status(asset.getStatus())
                .createdBy(asset.getCreatedBy() != null ? asset.getCreatedBy().getUsername() : null)
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
