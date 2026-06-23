package com.langcenter.assetmanagement.service;

import com.langcenter.assetmanagement.dto.transaction.*;
import com.langcenter.assetmanagement.entity.*;
import com.langcenter.assetmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse createBorrowRequest(BorrowRequest request) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy tài sản"));

        if (asset.getAvailableQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Số lượng tài sản khả dụng không đủ");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        Transaction transaction = Transaction.builder()
                .transactionCode("BRW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .asset(asset)
                .user(user)
                .transactionType("BORROW")
                .quantity(request.getQuantity())
                .note(request.getNote())
                .expectedReturn(request.getExpectedReturn())
                .status("PENDING")
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse approveBorrowRequest(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy giao dịch"));

        if (!"PENDING".equals(transaction.getStatus()) || !"BORROW".equals(transaction.getTransactionType())) {
            throw new IllegalArgumentException("Chỉ có thể duyệt yêu cầu mượn đang chờ (PENDING)");
        }

        String approverUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User approver = userRepository.findByUsername(approverUsername).orElseThrow();

        // Cập nhật trạng thái. Việc trừ quantity được trigger ở database lo
        transaction.setStatus("APPROVED");
        transaction.setApprovedBy(approver);
        transaction.setBorrowDate(LocalDateTime.now());

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse rejectBorrowRequest(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy giao dịch"));

        if (!"PENDING".equals(transaction.getStatus())) {
            throw new IllegalArgumentException("Chỉ có thể từ chối yêu cầu đang chờ (PENDING)");
        }

        String approverUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User approver = userRepository.findByUsername(approverUsername).orElseThrow();

        transaction.setStatus("REJECTED");
        transaction.setApprovedBy(approver);

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse returnAsset(Integer id, ReturnRequest request) {
        Transaction borrowTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy giao dịch mượn"));

        if (!"APPROVED".equals(borrowTransaction.getStatus()) || !"BORROW".equals(borrowTransaction.getTransactionType())) {
            throw new IllegalArgumentException("Giao dịch mượn chưa được duyệt hoặc đã hoàn tất/trả");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        // Tạo giao dịch trả
        Transaction returnTransaction = Transaction.builder()
                .transactionCode("RTN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .asset(borrowTransaction.getAsset())
                .user(user)
                .transactionType("RETURN")
                .quantity(borrowTransaction.getQuantity())
                .note(request.getNote())
                .status("PENDING")
                .build();

        return toResponse(transactionRepository.save(returnTransaction));
    }

    @Transactional
    public TransactionResponse approveReturn(Integer returnId) {
        Transaction returnTransaction = transactionRepository.findById(returnId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy giao dịch trả"));

        if (!"PENDING".equals(returnTransaction.getStatus()) || !"RETURN".equals(returnTransaction.getTransactionType())) {
            throw new IllegalArgumentException("Giao dịch trả không hợp lệ");
        }

        String approverUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User approver = userRepository.findByUsername(approverUsername).orElseThrow();

        // Cập nhật trạng thái trả thành COMPLETED. Việc cộng quantity được trigger ở DB
        returnTransaction.setStatus("COMPLETED");
        returnTransaction.setApprovedBy(approver);
        returnTransaction.setActualReturn(LocalDateTime.now());

        return toResponse(transactionRepository.save(returnTransaction));
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .assetName(transaction.getAsset().getName())
                .assetCode(transaction.getAsset().getAssetCode())
                .username(transaction.getUser().getUsername())
                .approvedBy(transaction.getApprovedBy() != null ? transaction.getApprovedBy().getUsername() : null)
                .transactionType(transaction.getTransactionType())
                .quantity(transaction.getQuantity())
                .note(transaction.getNote())
                .borrowDate(transaction.getBorrowDate())
                .expectedReturn(transaction.getExpectedReturn())
                .actualReturn(transaction.getActualReturn())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
