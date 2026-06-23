package com.langcenter.assetmanagement.repository;

import com.langcenter.assetmanagement.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserId(Integer userId);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByTransactionType(String transactionType);
}
