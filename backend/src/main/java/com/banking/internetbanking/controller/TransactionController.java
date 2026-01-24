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

/**
 * 取引コントローラークラス
 * 
 * 取引関連のREST APIエンドポイントを提供します。
 * 取引の取得、作成、更新、削除などの機能を実装します。
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * コンストラクタ
     * 
     * @param transactionService 取引サービス
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * すべての取引を取得します
     * 
     * @return 取引リスト
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    /**
     * IDで取引を取得します
     * 
     * @param id 取引ID
     * @return 取引エンティティ
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 口座IDで取引リストを取得します
     * 
     * @param accountId 口座ID
     * @return 取引リスト
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    /**
     * ユーザーIDで取引リストを取得します
     * 
     * @param userId ユーザーID
     * @return 取引リスト
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }

    /**
     * 口座IDと日付範囲で取引リストを取得します
     * 
     * @param accountId 口座ID
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 取引リスト
     */
    @GetMapping("/account/{accountId}/range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(accountId, startDate, endDate));
    }

    /**
     * 参照番号で取引を取得します
     * 
     * @param referenceNumber 参照番号
     * @return 取引エンティティ
     */
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<Transaction> getTransactionByReferenceNumber(@PathVariable String referenceNumber) {
        return transactionService.getTransactionByReferenceNumber(referenceNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 振込取引を作成します
     * 
     * @param request 振込情報（fromAccountId, toAccountId, amount, currency, description）
     * @return 作成された取引エンティティ
     */
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

    /**
     * 入金取引を作成します
     * 
     * @param request 入金情報（toAccountId, amount, currency, description）
     * @return 作成された取引エンティティ
     */
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

    /**
     * 出金取引を作成します
     * 
     * @param request 出金情報（fromAccountId, amount, currency, description）
     * @return 作成された取引エンティティ
     */
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

    /**
     * 取引情報を更新します
     * 
     * @param id 取引ID
     * @param transaction 更新する取引エンティティ
     * @return 更新された取引エンティティ
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        if (transactionService.updateTransaction(transaction)) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 取引を削除します
     * 
     * @param id 取引ID
     * @return 削除結果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        if (transactionService.deleteTransaction(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
