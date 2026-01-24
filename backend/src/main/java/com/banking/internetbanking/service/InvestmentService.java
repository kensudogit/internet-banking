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

/**
 * 投資サービスクラス
 * 
 * 投資関連のビジネスロジックを提供します。
 * 投資の作成、検索、更新、削除などの操作を行います。
 */
@Service
@Transactional
public class InvestmentService {

    private final InvestmentRepository investmentRepository;

    /**
     * コンストラクタ
     * 
     * @param investmentRepository 投資リポジトリ
     */
    public InvestmentService(InvestmentRepository investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    /**
     * すべての投資を取得します
     * 
     * @return 投資リスト
     */
    public List<Investment> getAllInvestments() {
        return investmentRepository.findAll();
    }

    /**
     * IDで投資を取得します
     * 
     * @param id 投資ID
     * @return 投資エンティティ（存在しない場合は空のOptional）
     */
    public Optional<Investment> getInvestmentById(Long id) {
        return investmentRepository.findById(id);
    }

    /**
     * ユーザーIDで投資リストを取得します
     * 
     * @param userId ユーザーID
     * @return 投資リスト
     */
    public List<Investment> getInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserId(userId);
    }

    /**
     * 口座IDで投資リストを取得します
     * 
     * @param accountId 口座ID
     * @return 投資リスト
     */
    public List<Investment> getInvestmentsByAccountId(Long accountId) {
        return investmentRepository.findByAccountId(accountId);
    }

    /**
     * ユーザーIDでアクティブな投資リストを取得します
     * 
     * @param userId ユーザーID
     * @return アクティブな投資のリスト
     */
    public List<Investment> getActiveInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }

    /**
     * 新しい投資を作成します
     * 
     * @param userId ユーザーID
     * @param accountId 口座ID
     * @param investmentType 投資種別
     * @param productName 商品名
     * @param amount 投資金額
     * @param currentValue 現在の評価額
     * @param purchaseDate 購入日
     * @param status ステータス
     * @return 作成された投資エンティティ
     */
    public Investment createInvestment(Long userId, Long accountId, String investmentType,
            String productName, BigDecimal amount, BigDecimal currentValue,
            LocalDate purchaseDate, String status) {
        Investment investment = new Investment(
                null, userId, accountId, investmentType, productName,
                amount, currentValue, purchaseDate, status,
                LocalDateTime.now(), LocalDateTime.now());
        return investmentRepository.save(investment);
    }

    /**
     * 投資情報を更新します
     * 
     * @param investment 更新する投資エンティティ
     * @return 更新成功の場合true
     */
    public boolean updateInvestment(Investment investment) {
        Investment updatedInvestment = new Investment(
                investment.getId(), investment.getUserId(), investment.getAccountId(),
                investment.getInvestmentType(), investment.getProductName(),
                investment.getAmount(), investment.getCurrentValue(),
                investment.getPurchaseDate(), investment.getStatus(),
                investment.getCreatedAt(), LocalDateTime.now());
        return investmentRepository.save(updatedInvestment) != null;
    }

    /**
     * 投資を削除します
     * 
     * @param id 投資ID
     * @return 削除成功の場合true
     */
    public boolean deleteInvestment(Long id) {
        Optional<Investment> investment = investmentRepository.findById(id);
        if (investment.isPresent()) {
            investmentRepository.delete(investment.get());
            return true;
        }
        return false;
    }
}
