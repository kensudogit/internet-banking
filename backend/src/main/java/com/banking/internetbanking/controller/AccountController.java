package com.banking.internetbanking.controller;

import com.banking.internetbanking.entity.Account;
import com.banking.internetbanking.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 口座コントローラークラス
 * 
 * 口座関連のREST APIエンドポイントを提供します。
 * 口座の取得、作成、更新、削除、振込などの機能を実装します。
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    private final AccountService accountService;

    /**
     * コンストラクタ
     * 
     * @param accountService 口座サービス
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * すべての口座を取得します
     * 
     * @return 口座リスト
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    /**
     * IDで口座を取得します
     * 
     * @param id 口座ID
     * @return 口座エンティティ
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ユーザーIDで口座リストを取得します
     * 
     * @param userId ユーザーID
     * @return 口座リスト
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    /**
     * 口座番号で口座を取得します
     * 
     * @param accountNumber 口座番号
     * @return 口座エンティティ
     */
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新しい口座を作成します
     * 
     * @param request 口座作成情報（userId, accountType, currency, interestRate）
     * @return 作成された口座エンティティ
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String accountType = (String) request.get("accountType");
            String currency = (String) request.get("currency");
            BigDecimal interestRate = new BigDecimal(request.get("interestRate").toString());

            Account account = accountService.createAccount(userId, accountType, currency, interestRate);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 口座情報を更新します
     * 
     * @param id 口座ID
     * @param account 更新する口座エンティティ
     * @return 更新された口座エンティティ
     */
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        if (accountService.updateAccount(account)) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 口座を削除します
     * 
     * @param id 口座ID
     * @return 削除結果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        if (accountService.deleteAccount(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 口座間で資金を振り込みます
     * 
     * @param request 振込情報（fromAccountId, toAccountId, amount）
     * @return 振込結果
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody Map<String, Object> request) {
        try {
            Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
            Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            if (accountService.transferMoney(fromAccountId, toAccountId, amount)) {
                return ResponseEntity.ok(Map.of("message", "送金が完了しました"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "送金に失敗しました"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 口座の残高を取得します
     * 
     * @param id 口座ID
     * @return 残高情報（accountId, balance, currency）
     */
    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getAccountBalance(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(account -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("accountId", account.getId());
                    response.put("balance", account.getBalance());
                    response.put("currency", account.getCurrency());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
