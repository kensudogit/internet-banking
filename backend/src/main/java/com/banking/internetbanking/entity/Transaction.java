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
 * 取引エンティティクラス
 * 
 * インターネットバンキングシステムの取引情報を表すエンティティです。
 * 振込、入金、出金、支払いなどの取引情報を保持します。
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    /** 取引ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 送金元口座ID */
    @Column(name = "from_account_id")
    private Long fromAccountId;

    /** 送金先口座ID */
    @Column(name = "to_account_id")
    private Long toAccountId;

    /** 取引種別（TRANSFER: 振込, DEPOSIT: 入金, WITHDRAWAL: 出金, PAYMENT: 支払い） */
    @Column(name = "transaction_type")
    private String transactionType;

    /** 取引金額 */
    @Column(name = "amount")
    private BigDecimal amount;

    /** 通貨（例: JPY, USD） */
    @Column(name = "currency")
    private String currency;

    /** 取引説明 */
    @Column(name = "description")
    private String description;

    /** ステータス（PENDING: 処理中, COMPLETED: 完了, FAILED: 失敗, CANCELLED: キャンセル） */
    @Column(name = "status")
    private String status;

    /** 参照番号 */
    @Column(name = "reference_number")
    private String referenceNumber;

    /** 取引日時 */
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    /** 作成日時 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * JPA用のデフォルトコンストラクタ
     * Hibernateがエンティティをインスタンス化するために必要です。
     */
    protected Transaction() {
    }

    /**
     * 取引エンティティのコンストラクタ
     * 
     * @param id 取引ID
     * @param fromAccountId 送金元口座ID
     * @param toAccountId 送金先口座ID
     * @param transactionType 取引種別
     * @param amount 取引金額
     * @param currency 通貨
     * @param description 取引説明
     * @param status ステータス
     * @param referenceNumber 参照番号
     * @param transactionDate 取引日時
     * @param createdAt 作成日時
     */
    public Transaction(Long id, Long fromAccountId, Long toAccountId, String transactionType,
            BigDecimal amount, String currency, String description, String status,
            String referenceNumber, LocalDateTime transactionDate, LocalDateTime createdAt) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }

    // ========== Getters ==========
    
    /**
     * 取引IDを取得します
     * 
     * @return 取引ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 送金元口座IDを取得します
     * 
     * @return 送金元口座ID
     */
    public Long getFromAccountId() {
        return fromAccountId;
    }

    /**
     * 送金先口座IDを取得します
     * 
     * @return 送金先口座ID
     */
    public Long getToAccountId() {
        return toAccountId;
    }

    /**
     * 取引種別を取得します
     * 
     * @return 取引種別
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * 取引金額を取得します
     * 
     * @return 取引金額
     */
    public BigDecimal getAmount() {
        return amount;
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
     * 取引説明を取得します
     * 
     * @return 取引説明
     */
    public String getDescription() {
        return description;
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
     * 参照番号を取得します
     * 
     * @return 参照番号
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * 取引日時を取得します
     * 
     * @return 取引日時
     */
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    /**
     * 作成日時を取得します
     * 
     * @return 作成日時
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
