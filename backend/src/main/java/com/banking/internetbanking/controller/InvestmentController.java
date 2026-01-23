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

@RestController
@RequestMapping("/api/investments")
@CrossOrigin(origins = "http://localhost:3000")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping
    public ResponseEntity<List<Investment>> getAllInvestments() {
        return ResponseEntity.ok(investmentService.getAllInvestments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investment> getInvestmentById(@PathVariable Long id) {
        return investmentService.getInvestmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Investment>> getInvestmentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByUserId(userId));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Investment>> getActiveInvestmentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.getActiveInvestmentsByUserId(userId));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Investment>> getInvestmentsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(investmentService.getInvestmentsByAccountId(accountId));
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<Investment> updateInvestment(@PathVariable Long id, @RequestBody Investment investment) {
        if (investmentService.updateInvestment(investment)) {
            return ResponseEntity.ok(investment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvestment(@PathVariable Long id) {
        if (investmentService.deleteInvestment(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
