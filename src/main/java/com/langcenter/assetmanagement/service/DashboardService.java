package com.langcenter.assetmanagement.service;

import com.langcenter.assetmanagement.dto.dashboard.DashboardResponse;
import com.langcenter.assetmanagement.entity.Asset;
import com.langcenter.assetmanagement.repository.AssetRepository;
import com.langcenter.assetmanagement.repository.TransactionRepository;
import com.langcenter.assetmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AssetRepository assetRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardResponse getDashboardStatistics() {
        List<Asset> assets = assetRepository.findAll();

        long totalAssets = assets.stream().mapToLong(Asset::getTotalQuantity).sum();
        long totalAvailable = assets.stream().mapToLong(Asset::getAvailableQuantity).sum();
        long totalBorrowed = totalAssets - totalAvailable;
        long totalUsers = userRepository.count();

        long pendingTransactions = transactionRepository.findByStatus("PENDING").size();

        Map<String, Long> assetsByCategory = assets.stream()
                .filter(a -> a.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Asset::getCategory,
                        Collectors.summingLong(Asset::getTotalQuantity)
                ));

        return DashboardResponse.builder()
                .totalAssets(totalAssets)
                .totalAvailableAssets(totalAvailable)
                .totalBorrowedAssets(totalBorrowed)
                .totalUsers(totalUsers)
                .pendingTransactions(pendingTransactions)
                .assetsByCategory(assetsByCategory)
                .build();
    }
}
