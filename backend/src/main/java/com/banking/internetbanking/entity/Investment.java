package com.banking.internetbanking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 投資エンティティクラス
 * 
 * インターネットバンキングシステムの投資情報を表すエンティティです。
 * 投資信託、株式、債券、ETFなどの投資商品情報を保持します。
 */
@Entity
@Table(name = "investments")
public class Investment {

    /** 投資ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** ユーザーID（外部キー） */
    @Column(name = "user_id")
    private Long userId;

    /** 口座ID（外部キー） */
    @Column(name = "account_id")
    private Long accountId;

    /** 投資種別（MUTUAL_FUND: 投資信託, STOCK: 株式, BOND: 債券, ETF: ETF） */
    @Column(name = "investment_type")
    private String investmentType;

    /** 商品名 */
    @Column(name = "product_name")
    private String productName;

    /** 投資金額 */
    @Column(name = "amount")
    private BigDecimal amount;

    /** 現在の評価額 */
    @Column(name = "current_value")
    private BigDecimal currentValue;

    /** 購入日 */
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    /** ステータス（ACTIVE: 保有中, SOLD: 売却済み, MATURED: 満期） */
    @Column(name = "status")
    private String status;

    /** 作成日時 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 更新日時 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA用のデフォルトコンストラクタ
     * Hibernateがエンティティをインスタンス化するために必要です。
     */
    protected Investment() {
    }

    /**
     * 投資エンティティのコンストラクタ
     * 
     * @param id 投資ID
     * @param userId ユーザーID
     * @param accountId 口座ID
     * @param investmentType 投資種別
     * @param productName 商品名
     * @param amount 投資金額
     * @param currentValue 現在の評価額
     * @param purchaseDate 購入日
     * @param status ステータス
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public Investment(Long id, Long userId, Long accountId, String investmentType,
            String productName, BigDecimal amount, BigDecimal currentValue,
            LocalDate purchaseDate, String status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.investmentType = investmentType;
        this.productName = productName;
        this.amount = amount;
        this.currentValue = currentValue;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Getters ==========
    
    /**
     * 投資IDを取得します
     * 
     * @return 投資ID
     */
    public Long getId() {
        return id;
    }

    /**
     * ユーザーIDを取得します
     * 
     * @return ユーザーID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 口座IDを取得します
     * 
     * @return 口座ID
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * 投資種別を取得します
     * 
     * @return 投資種別
     */
    public String getInvestmentType() {
        return investmentType;
    }

    /**
     * 商品名を取得します
     * 
     * @return 商品名
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 投資金額を取得します
     * 
     * @return 投資金額
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 現在の評価額を取得します
     * 
     * @return 現在の評価額
     */
    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    /**
     * 購入日を取得します
     * 
     * @return 購入日
     */
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * ステータスを取得します
     * 
     * @return ステータス
     */
    public String getStatus() {
        return status;
    }

    /**
     * 作成日時を取得します
     * 
     * @return 作成日時
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 更新日時を取得します
     * 
     * @return 更新日時
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
