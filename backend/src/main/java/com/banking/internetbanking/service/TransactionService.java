package com.banking.internetbanking.service;

import com.banking.internetbanking.repository.TransactionRepository;
import com.banking.internetbanking.entity.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        // ユーザーIDから口座IDのリストを取得
        List<Long> accountIds = accountService.getAccountsByUserId(userId).stream()
                .map(account -> account.getId())
                .collect(Collectors.toList());

        // ユーザーの口座に関連する取引を取得
        return transactionRepository.findAll().stream()
                .filter(t -> {
                    Long fromAccountId = t.getFromAccountId();
                    Long toAccountId = t.getToAccountId();
                    return (fromAccountId != null && accountIds.contains(fromAccountId)) ||
                            (toAccountId != null && accountIds.contains(toAccountId));
                })
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(Long accountId, LocalDateTime startDate,
            LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    public Optional<Transaction> getTransactionByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber);
    }

    public Transaction createTransaction(Long fromAccountId, Long toAccountId, String transactionType,
            BigDecimal amount, String currency, String description) {
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = new Transaction(
                null, fromAccountId, toAccountId, transactionType,
                amount, currency, description, "COMPLETED", referenceNumber,
                LocalDateTime.now(), LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public Transaction createDepositTransaction(Long toAccountId, BigDecimal amount, String currency,
            String description) {
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = new Transaction(
                null, null, toAccountId, "DEPOSIT",
                amount, currency, description, "COMPLETED", referenceNumber,
                LocalDateTime.now(), LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public Transaction createWithdrawalTransaction(Long fromAccountId, BigDecimal amount, String currency,
            String description) {
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = new Transaction(
                null, fromAccountId, null, "WITHDRAWAL",
                amount, currency, description, "COMPLETED", referenceNumber,
                LocalDateTime.now(), LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public boolean updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction) != null;
    }

    public boolean deleteTransaction(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            transactionRepository.delete(transaction.get());
            return true;
        }
        return false;
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
