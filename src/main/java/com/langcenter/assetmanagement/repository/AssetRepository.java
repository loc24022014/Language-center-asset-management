package com.langcenter.assetmanagement.repository;

import com.langcenter.assetmanagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Integer> {
    Optional<Asset> findByAssetCode(String assetCode);
    boolean existsByAssetCode(String assetCode);
    List<Asset> findByStatus(String status);
    List<Asset> findByCategory(String category);
}
