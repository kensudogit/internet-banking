package com.banking.internetbanking.service;

import com.banking.internetbanking.repository.UserRepository;
import com.banking.internetbanking.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ユーザーサービスクラス
 * 
 * ユーザー関連のビジネスロジックを提供します。
 * ユーザーの作成、検索、更新、削除、認証などの操作を行います。
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * コンストラクタ
     * 
     * @param userRepository ユーザーリポジトリ
     * @param passwordEncoder パスワードエンコーダー
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * すべてのユーザーを取得します
     * 
     * @return ユーザーリスト
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * IDでユーザーを取得します
     * 
     * @param id ユーザーID
     * @return ユーザーエンティティ（存在しない場合は空のOptional）
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * ユーザー名でユーザーを取得します
     * 
     * @param username ユーザー名
     * @return ユーザーエンティティ（存在しない場合は空のOptional）
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * メールアドレスでユーザーを取得します
     * 
     * @param email メールアドレス
     * @return ユーザーエンティティ（存在しない場合は空のOptional）
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 新しいユーザーを作成します
     * 
     * @param username ユーザー名
     * @param email メールアドレス
     * @param password パスワード（ハッシュ化されます）
     * @param firstName 名
     * @param lastName 姓
     * @param phoneNumber 電話番号
     * @return 作成されたユーザーエンティティ
     */
    public User createUser(String username, String email, String password,
            String firstName, String lastName, String phoneNumber) {
        User user = new User(
                null, username, email, passwordEncoder.encode(password),
                firstName, lastName, phoneNumber, true, false, false,
                null, null, LocalDateTime.now(), LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * ユーザー情報を更新します
     * 
     * @param user 更新するユーザーエンティティ
     * @return 更新成功の場合true
     */
    public boolean updateUser(User user) {
        return userRepository.save(user) != null;
    }

    /**
     * ユーザーを削除します
     * 
     * @param id ユーザーID
     * @return 削除成功の場合true
     */
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    /**
     * ユーザー認証を行います
     * 
     * @param username ユーザー名
     * @param password パスワード
     * @return 認証成功の場合true
     */
    public boolean authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() &&
                user.get().isEnabled() &&
                !user.get().isLocked() &&
                passwordEncoder.matches(password, user.get().getPasswordHash());
    }

    /**
     * 最終ログイン日時を更新します
     * 
     * @param userId ユーザーID
     */
    public void updateLastLogin(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User updatedUser = new User(
                    user.get().getId(), user.get().getUsername(), user.get().getEmail(),
                    user.get().getPasswordHash(), user.get().getFirstName(), user.get().getLastName(),
                    user.get().getPhoneNumber(), user.get().isEnabled(), user.get().isLocked(),
                    user.get().isMfaEnabled(), user.get().getMfaSecret(), LocalDateTime.now(),
                    user.get().getCreatedAt(), LocalDateTime.now());
            userRepository.save(updatedUser);
        }
    }
}
