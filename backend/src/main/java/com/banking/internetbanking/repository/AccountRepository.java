package com.banking.internetbanking.repository;

import com.banking.internetbanking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 口座リポジトリインターフェース
 * 
 * 口座エンティティに対するデータベース操作を提供します。
 * Spring Data JPAの機能を使用して、口座情報の検索・保存を行います。
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * ユーザーIDで口座リストを検索します
     * 
     * @param userId ユーザーID
     * @return 該当する口座のリスト
     */
    List<Account> findByUserId(Long userId);

    /**
     * 口座番号で口座を検索します
     * 
     * @param accountNumber 口座番号
     * @return 口座エンティティ（存在しない場合は空のOptional）
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * ユーザーIDとステータスで口座リストを検索します
     * 
     * @param userId ユーザーID
     * @param status ステータス（ACTIVE, SUSPENDED, CLOSED）
     * @return 該当する口座のリスト
     */
    List<Account> findByUserIdAndStatus(Long userId, String status);
}
