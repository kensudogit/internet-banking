package com.banking.internetbanking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "from_account_id")
    private Long fromAccountId;

    @Column(name = "to_account_id")
    private Long toAccountId;

    @Column(name = "transaction_type")
    private String transactionType; // TRANSFER, DEPOSIT, WITHDRAWAL, PAYMENT

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // JPA用のデフォルトコンストラクタ（Hibernateがエンティティをインスタンス化するために必要）
    protected Transaction() {
    }

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

    // Getters
    public Long getId() {
        return id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
