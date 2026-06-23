package com.langcenter.assetmanagement.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private long totalAssets;
    private long totalAvailableAssets;
    private long totalBorrowedAssets;
    private long totalUsers;
    private long pendingTransactions;
    private Map<String, Long> assetsByCategory;
}
