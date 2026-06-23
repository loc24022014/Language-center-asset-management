package com.langcenter.assetmanagement.service;

import com.langcenter.assetmanagement.dto.transaction.BorrowRequest;
import com.langcenter.assetmanagement.dto.transaction.TransactionResponse;
import com.langcenter.assetmanagement.entity.Asset;
import com.langcenter.assetmanagement.entity.Transaction;
import com.langcenter.assetmanagement.entity.User;
import com.langcenter.assetmanagement.repository.AssetRepository;
import com.langcenter.assetmanagement.repository.TransactionRepository;
import com.langcenter.assetmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Asset mockAsset;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockAsset = Asset.builder()
                .id(1)
                .assetCode("PROJ-001")
                .name("Máy chiếu")
                .availableQuantity(5)
                .build();

        mockUser = User.builder()
                .username("teacher1")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("teacher1", "password")
        );
    }

    @Test
    void testCreateBorrowRequest_Success() {
        BorrowRequest request = new BorrowRequest();
        request.setAssetId(1);
        request.setQuantity(2);

        when(assetRepository.findById(1)).thenReturn(Optional.of(mockAsset));
        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.of(mockUser));

        Transaction savedTransaction = Transaction.builder()
                .id(100)
                .transactionCode("BRW-123456")
                .asset(mockAsset)
                .user(mockUser)
                .transactionType("BORROW")
                .quantity(2)
                .status("PENDING")
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponse response = transactionService.createBorrowRequest(request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals(2, response.getQuantity());
        assertEquals("BORROW", response.getTransactionType());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateBorrowRequest_Fail_NotEnoughQuantity() {
        BorrowRequest request = new BorrowRequest();
        request.setAssetId(1);
        request.setQuantity(10); // More than available 5

        when(assetRepository.findById(1)).thenReturn(Optional.of(mockAsset));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createBorrowRequest(request);
        });

        assertEquals("Số lượng tài sản khả dụng không đủ", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
