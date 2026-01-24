package com.banking.internetbanking.service;

import com.banking.internetbanking.repository.TransactionRepository;
import com.banking.internetbanking.entity.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 取引サービスクラス
 * 
 * 取引関連のビジネスロジックを提供します。
 * 取引の作成、検索、更新、削除などの操作を行います。
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    /**
     * コンストラクタ
     * 
     * @param transactionRepository 取引リポジトリ
     * @param accountService 口座サービス
     */
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    /**
     * すべての取引を取得します
     * 
     * @return 取引リスト
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * IDで取引を取得します
     * 
     * @param id 取引ID
     * @return 取引エンティティ（存在しない場合は空のOptional）
     */
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * 口座IDで取引リストを取得します
     * 
     * @param accountId 口座ID
     * @return 取引リスト
     */
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    /**
     * ユーザーIDで取引リストを取得します
     * ユーザーが所有するすべての口座に関連する取引を取得します。
     * 
     * @param userId ユーザーID
     * @return 取引リスト
     */
    public List<Transaction> getTransactionsByUserId(Long userId) {
        // ユーザーIDから口座IDのリストを取得
        List<Long> accountIds = accountService.getAccountsByUserId(userId).stream()
                .map(account -> account.getId())
                .collect(Collectors.toList());

        // ユーザーの口座に関連する取引を取得
        return transactionRepository.findAll().stream()
                .filter(t -> {
                    Long fromAccountId = t.getFromAccountId();
                    Long toAccountId = t.getToAccountId();
                    return (fromAccountId != null && accountIds.contains(fromAccountId)) ||
                            (toAccountId != null && accountIds.contains(toAccountId));
                })
                .collect(Collectors.toList());
    }

    /**
     * 口座IDと日付範囲で取引リストを取得します
     * 
     * @param accountId 口座ID
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 取引リスト
     */
    public List<Transaction> getTransactionsByDateRange(Long accountId, LocalDateTime startDate,
            LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    /**
     * 参照番号で取引を取得します
     * 
     * @param referenceNumber 参照番号
     * @return 取引エンティティ（存在しない場合は空のOptional）
     */
    public Optional<Transaction> getTransactionByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber);
    }

    /**
     * 新しい取引を作成します
     * 
     * @param fromAccountId 送金元口座ID
     * @param toAccountId 送金先口座ID
     * @param transactionType 取引種別
     * @param amount 取引金額
     * @param currency 通貨
     * @param description 取引説明
     * @return 作成された取引エンティティ
     */
    public Transaction createTransaction(Long fromAccountId, Long toAccountId, String transactionType,
            BigDecimal amount, String currency, String description) {
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = new Transaction(
                null, fromAccountId, toAccountId, transactionType,
                amount, currency, description, "COMPLETED", referenceNumber,
                LocalDateTime.now(), LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    /**
     * 入金取引を作成します
     * 
     * @param toAccountId 入金先口座ID
     * @param amount 入金額
     * @param currency 通貨
     * @param description 取引説明
     * @return 作成された取引エンティティ
     */
    public Transaction createDepositTransaction(Long toAccountId, BigDecimal amount, String currency,
            String description) {
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = new Transaction(
                null, null, toAccountId, "DEPOSIT",
                amount, currency, description, "COMPLETED", referenceNumber,
                LocalDateTime.now(), LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    /**
     * 出金取引を作成します
     * 
     * @param fromAccountId 出金元口座ID
     * @param amount 出金額
     * @param currency 通貨
     * @param description 取引説明
     * @return 作成された取引エンティティ
     */
    public Transaction createWithdrawalTransaction(Long fromAccountId, BigDecimal amount, String currency,
            String description) {
        String referenceNumber = generateReferenceNumber();
        Transaction transaction = new Transaction(
                null, fromAccountId, null, "WITHDRAWAL",
                amount, currency, description, "COMPLETED", referenceNumber,
                LocalDateTime.now(), LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    /**
     * 取引情報を更新します
     * 
     * @param transaction 更新する取引エンティティ
     * @return 更新成功の場合true
     */
    public boolean updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction) != null;
    }

    /**
     * 取引を削除します
     * 
     * @param id 取引ID
     * @return 削除成功の場合true
     */
    public boolean deleteTransaction(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            transactionRepository.delete(transaction.get());
            return true;
        }
        return false;
    }

    /**
     * 参照番号を生成します
     * 
     * @return 生成された参照番号
     */
    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
