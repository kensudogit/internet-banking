package com.banking.internetbanking.controller;

import com.banking.internetbanking.entity.Transaction;
import com.banking.internetbanking.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }

    @GetMapping("/account/{accountId}/range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(accountId, startDate, endDate));
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<Transaction> getTransactionByReferenceNumber(@PathVariable String referenceNumber) {
        return transactionService.getTransactionByReferenceNumber(referenceNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> createTransfer(@RequestBody Map<String, Object> request) {
        try {
            Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
            Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.get("currency");
            String description = (String) request.get("description");

            Transaction transaction = transactionService.createTransaction(
                    fromAccountId, toAccountId, "TRANSFER", amount, currency, description);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> createDeposit(@RequestBody Map<String, Object> request) {
        try {
            Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.get("currency");
            String description = (String) request.get("description");

            Transaction transaction = transactionService.createDepositTransaction(
                    toAccountId, amount, currency, description);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<Transaction> createWithdrawal(@RequestBody Map<String, Object> request) {
        try {
            Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String currency = (String) request.get("currency");
            String description = (String) request.get("description");

            Transaction transaction = transactionService.createWithdrawalTransaction(
                    fromAccountId, amount, currency, description);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        if (transactionService.updateTransaction(transaction)) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        if (transactionService.deleteTransaction(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
