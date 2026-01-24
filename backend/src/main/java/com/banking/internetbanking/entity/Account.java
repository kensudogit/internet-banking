package com.banking.internetbanking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 口座エンティティクラス
 * 
 * インターネットバンキングシステムの口座情報を表すエンティティです。
 * 口座番号、口座種別、残高、通貨、ステータス、金利などを保持します。
 */
@Entity
@Table(name = "accounts")
public class Account {

    /** 口座ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** ユーザーID（外部キー） */
    @Column(name = "user_id")
    private Long userId;

    /** 口座番号 */
    @Column(name = "account_number")
    private String accountNumber;

    /** 口座種別（SAVINGS: 普通預金, CHECKING: 当座預金, FIXED_DEPOSIT: 定期預金） */
    @Column(name = "account_type")
    private String accountType;

    /** 残高 */
    @Column(name = "balance")
    private BigDecimal balance;

    /** 通貨（例: JPY, USD） */
    @Column(name = "currency")
    private String currency;

    /** ステータス（ACTIVE: 有効, SUSPENDED: 停止, CLOSED: 閉鎖） */
    @Column(name = "status")
    private String status;

    /** 金利 */
    @Column(name = "interest_rate")
    private BigDecimal interestRate;

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
    protected Account() {
    }

    /**
     * 口座エンティティのコンストラクタ
     * 
     * @param id 口座ID
     * @param userId ユーザーID
     * @param accountNumber 口座番号
     * @param accountType 口座種別
     * @param balance 残高
     * @param currency 通貨
     * @param status ステータス
     * @param interestRate 金利
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public Account(Long id, Long userId, String accountNumber, String accountType,
            BigDecimal balance, String currency, String status,
            BigDecimal interestRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
        this.interestRate = interestRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Getters ==========
    
    /**
     * 口座IDを取得します
     * 
     * @return 口座ID
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
     * 口座番号を取得します
     * 
     * @return 口座番号
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * 口座種別を取得します
     * 
     * @return 口座種別
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * 残高を取得します
     * 
     * @return 残高
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * 通貨を取得します
     * 
     * @return 通貨
     */
    public String getCurrency() {
        return currency;
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
     * 金利を取得します
     * 
     * @return 金利
     */
    public BigDecimal getInterestRate() {
        return interestRate;
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
