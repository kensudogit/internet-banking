package com.banking.internetbanking.repository;

import com.banking.internetbanking.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 投資リポジトリインターフェース
 * 
 * 投資エンティティに対するデータベース操作を提供します。
 * Spring Data JPAの機能を使用して、投資情報の検索・保存を行います。
 */
@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    /**
     * ユーザーIDで投資リストを検索します
     * 
     * @param userId ユーザーID
     * @return 該当する投資のリスト
     */
    List<Investment> findByUserId(Long userId);

    /**
     * 口座IDで投資リストを検索します
     * 
     * @param accountId 口座ID
     * @return 該当する投資のリスト
     */
    List<Investment> findByAccountId(Long accountId);

    /**
     * ユーザーIDとステータスで投資リストを検索します
     * 
     * @param userId ユーザーID
     * @param status ステータス（ACTIVE, SOLD, MATURED）
     * @return 該当する投資のリスト
     */
    List<Investment> findByUserIdAndStatus(Long userId, String status);
}
