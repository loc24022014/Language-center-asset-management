package com.langcenter.assetmanagement.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetResponse {
    private Integer id;
    private String assetCode;
    private String name;
    private String category;
    private String description;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private String unit;
    private String location;
    private String status;
    private String createdBy; // username
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
