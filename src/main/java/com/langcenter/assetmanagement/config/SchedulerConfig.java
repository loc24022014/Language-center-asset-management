package com.langcenter.assetmanagement.config;

import com.langcenter.assetmanagement.entity.Transaction;
import com.langcenter.assetmanagement.repository.TransactionRepository;
import com.langcenter.assetmanagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    // Chạy vào lúc 08:00 sáng mỗi ngày
    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduleOverdueReminders() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Transaction> allTransactions = transactionRepository.findAll();
        
        for (Transaction t : allTransactions) {
            if ("APPROVED".equals(t.getStatus()) && "BORROW".equals(t.getTransactionType()) 
                && t.getExpectedReturn() != null && t.getExpectedReturn().isBefore(now)) {
                
                String toEmail = t.getUser().getEmail();
                if (toEmail != null && !toEmail.isEmpty()) {
                    String subject = "Nhắc nhở trả tài sản: " + t.getAsset().getName();
                    String text = "Xin chào " + t.getUser().getFullName() + ",\n\n" +
                                  "Tài sản " + t.getAsset().getName() + " (Mã phiếu: " + t.getTransactionCode() + 
                                  ") đã quá hạn trả (" + t.getExpectedReturn() + ").\n" +
                                  "Vui lòng hoàn trả sớm nhất có thể.\n\n" +
                                  "Trân trọng,\nBQL Trung tâm.";
                    
                    emailService.sendReminderEmail(toEmail, subject, text);
                }
            }
        }
    }
}
