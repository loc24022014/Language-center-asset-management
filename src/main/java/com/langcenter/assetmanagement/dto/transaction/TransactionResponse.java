package com.langcenter.assetmanagement.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Integer id;
    private String transactionCode;
    private String assetName;
    private String assetCode;
    private String username;
    private String approvedBy;
    private String transactionType;
    private Integer quantity;
    private String note;
    private LocalDateTime borrowDate;
    private LocalDateTime expectedReturn;
    private LocalDateTime actualReturn;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
