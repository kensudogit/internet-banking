package com.banking.internetbanking.service;

import com.banking.internetbanking.repository.InvestmentRepository;
import com.banking.internetbanking.entity.Investment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvestmentService {

    private final InvestmentRepository investmentRepository;

    public InvestmentService(InvestmentRepository investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    public List<Investment> getAllInvestments() {
        return investmentRepository.findAll();
    }

    public Optional<Investment> getInvestmentById(Long id) {
        return investmentRepository.findById(id);
    }

    public List<Investment> getInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserId(userId);
    }

    public List<Investment> getInvestmentsByAccountId(Long accountId) {
        return investmentRepository.findByAccountId(accountId);
    }

    public List<Investment> getActiveInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }

    public Investment createInvestment(Long userId, Long accountId, String investmentType,
            String productName, BigDecimal amount, BigDecimal currentValue,
            LocalDate purchaseDate, String status) {
        Investment investment = new Investment(
                null, userId, accountId, investmentType, productName,
                amount, currentValue, purchaseDate, status,
                LocalDateTime.now(), LocalDateTime.now());
        return investmentRepository.save(investment);
    }

    public boolean updateInvestment(Investment investment) {
        Investment updatedInvestment = new Investment(
                investment.getId(), investment.getUserId(), investment.getAccountId(),
                investment.getInvestmentType(), investment.getProductName(),
                investment.getAmount(), investment.getCurrentValue(),
                investment.getPurchaseDate(), investment.getStatus(),
                investment.getCreatedAt(), LocalDateTime.now());
        return investmentRepository.save(updatedInvestment) != null;
    }

    public boolean deleteInvestment(Long id) {
        Optional<Investment> investment = investmentRepository.findById(id);
        if (investment.isPresent()) {
            investmentRepository.delete(investment.get());
            return true;
        }
        return false;
    }
}
