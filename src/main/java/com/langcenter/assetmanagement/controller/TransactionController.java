package com.langcenter.assetmanagement.controller;

import com.langcenter.assetmanagement.dto.transaction.*;
import com.langcenter.assetmanagement.service.TransactionService;
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
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Quản lý mượn trả tài sản")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy tất cả giao dịch", description = "Chỉ ADMIN hoặc MANAGER được phép")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping("/borrow")
    @Operation(summary = "Tạo yêu cầu mượn tài sản")
    public ResponseEntity<TransactionResponse> requestBorrow(@Valid @RequestBody BorrowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createBorrowRequest(request));
    }

    @PutMapping("/{id}/approve-borrow")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Duyệt yêu cầu mượn", description = "Chỉ ADMIN hoặc MANAGER được phép")
    public ResponseEntity<TransactionResponse> approveBorrow(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionService.approveBorrowRequest(id));
    }

    @PutMapping("/{id}/reject-borrow")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Từ chối yêu cầu mượn", description = "Chỉ ADMIN hoặc MANAGER được phép")
    public ResponseEntity<TransactionResponse> rejectBorrow(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionService.rejectBorrowRequest(id));
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Tạo yêu cầu trả tài sản (từ giao dịch mượn)")
    public ResponseEntity<TransactionResponse> requestReturn(@PathVariable Integer id, @RequestBody ReturnRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.returnAsset(id, request));
    }

    @PutMapping("/{id}/approve-return")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Xác nhận nhận lại tài sản (duyệt trả)", description = "Chỉ ADMIN hoặc MANAGER được phép")
    public ResponseEntity<TransactionResponse> approveReturn(@PathVariable Integer id) {
        return ResponseEntity.ok(transactionService.approveReturn(id));
    }
}
