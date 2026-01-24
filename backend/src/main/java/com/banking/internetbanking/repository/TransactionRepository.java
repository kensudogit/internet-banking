package com.banking.internetbanking.repository;

import com.banking.internetbanking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 取引リポジトリインターフェース
 * 
 * 取引エンティティに対するデータベース操作を提供します。
 * Spring Data JPAの機能を使用して、取引情報の検索・保存を行います。
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 送金元または送金先の口座IDで取引リストを検索します
     * 
     * @param fromAccountId 送金元口座ID
     * @param toAccountId 送金先口座ID
     * @return 該当する取引のリスト
     */
    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);

    /**
     * 口座IDで取引リストを検索します（送金元または送金先が該当口座）
     * 
     * @param accountId 口座ID
     * @return 該当する取引のリスト
     */
    @Query("SELECT t FROM Transaction t WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);

    /**
     * 口座IDと日付範囲で取引リストを検索します
     * 
     * @param accountId 口座ID
     * @param startDate 開始日時
     * @param endDate 終了日時
     * @return 該当する取引のリスト
     */
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccountId = :accountId OR t.toAccountId = :accountId) AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 参照番号で取引を検索します
     * 
     * @param referenceNumber 参照番号
     * @return 取引エンティティ（存在しない場合は空のOptional）
     */
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
}
