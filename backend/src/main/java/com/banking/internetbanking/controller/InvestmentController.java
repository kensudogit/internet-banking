package com.banking.internetbanking.controller;

import com.banking.internetbanking.entity.Investment;
import com.banking.internetbanking.service.InvestmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 投資コントローラークラス
 * 
 * 投資関連のREST APIエンドポイントを提供します。
 * 投資の取得、作成、更新、削除などの機能を実装します。
 */
@RestController
@RequestMapping("/api/investments")
// CORS設定はSecurityConfigで一元管理するため、@CrossOriginは削除
public class InvestmentController {

    private final InvestmentService investmentService;

    /**
     * コンストラクタ
     * 
     * @param investmentService 投資サービス
     */
    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    /**
     * すべての投資を取得します
     * 
     * @return 投資リスト
     */
    @GetMapping
    public ResponseEntity<List<Investment>> getAllInvestments() {
        return ResponseEntity.ok(investmentService.getAllInvestments());
    }

    /**
     * IDで投資を取得します
     * 
     * @param id 投資ID
     * @return 投資エンティティ
     */
    @GetMapping("/{id}")
    public ResponseEntity<Investment> getInvestmentById(@PathVariable Long id) {
        return investmentService.getInvestmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ユーザーIDで投資リストを取得します
     * 
     * @param userId ユーザーID
     * @return 投資リスト
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Investment>> getInvestmentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByUserId(userId));
    }

    /**
     * ユーザーIDでアクティブな投資リストを取得します
     * 
     * @param userId ユーザーID
     * @return アクティブな投資のリスト
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Investment>> getActiveInvestmentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getActiveInvestmentsByUserId(userId));
    }

    /**
     * 口座IDで投資リストを取得します
     * 
     * @param accountId 口座ID
     * @return 投資リスト
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Investment>> getInvestmentsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByAccountId(accountId));
    }

    /**
     * 新しい投資を作成します
     * 
     * @param request 投資作成情報（userId, accountId, investmentType, productName, amount, currentValue, purchaseDate, status）
     * @return 作成された投資エンティティ
     */
    @PostMapping
    public ResponseEntity<Investment> createInvestment(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long accountId = Long.valueOf(request.get("accountId").toString());
            String investmentType = (String) request.get("investmentType");
            String productName = (String) request.get("productName");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            BigDecimal currentValue = new BigDecimal(request.get("currentValue").toString());
            LocalDate purchaseDate = LocalDate.parse(request.get("purchaseDate").toString());
            String status = (String) request.getOrDefault("status", "ACTIVE");

            Investment investment = investmentService.createInvestment(
                    userId, accountId, investmentType, productName,
                    amount, currentValue, purchaseDate, status);
            return ResponseEntity.ok(investment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 投資情報を更新します
     * 
     * @param id 投資ID
     * @param investment 更新する投資エンティティ
     * @return 更新された投資エンティティ
     */
    @PutMapping("/{id}")
    public ResponseEntity<Investment> updateInvestment(@PathVariable Long id, @RequestBody Investment investment) {
        if (investmentService.updateInvestment(investment)) {
            return ResponseEntity.ok(investment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 投資を削除します
     * 
     * @param id 投資ID
     * @return 削除結果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvestment(@PathVariable Long id) {
        if (investmentService.deleteInvestment(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
