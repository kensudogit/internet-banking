package com.banking.internetbanking.repository;

import com.banking.internetbanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーリポジトリインターフェース
 * 
 * ユーザーエンティティに対するデータベース操作を提供します。
 * Spring Data JPAの機能を使用して、ユーザー情報の検索・保存を行います。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ユーザー名でユーザーを検索します
     * 
     * @param username ユーザー名
     * @return ユーザーエンティティ（存在しない場合は空のOptional）
     */
    Optional<User> findByUsername(String username);

    /**
     * メールアドレスでユーザーを検索します
     * 
     * @param email メールアドレス
     * @return ユーザーエンティティ（存在しない場合は空のOptional）
     */
    Optional<User> findByEmail(String email);

    /**
     * ユーザー名が存在するかどうかを確認します
     * 
     * @param username ユーザー名
     * @return 存在する場合true、存在しない場合false
     */
    boolean existsByUsername(String username);

    /**
     * メールアドレスが存在するかどうかを確認します
     * 
     * @param email メールアドレス
     * @return 存在する場合true、存在しない場合false
     */
    boolean existsByEmail(String email);
}
