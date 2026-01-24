package com.banking.internetbanking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

/**
 * ユーザーエンティティクラス
 * 
 * インターネットバンキングシステムのユーザー情報を表すエンティティです。
 * ユーザー名、メールアドレス、パスワードハッシュ、個人情報、MFA設定などを保持します。
 */
@Entity
@Table(name = "users")
public class User {

    /** ユーザーID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** ユーザー名 */
    @Column(name = "username")
    private String username;

    /** メールアドレス */
    @Column(name = "email")
    private String email;

    /** パスワードハッシュ */
    @Column(name = "password_hash")
    private String passwordHash;

    /** 名 */
    @Column(name = "first_name")
    private String firstName;

    /** 姓 */
    @Column(name = "last_name")
    private String lastName;

    /** 電話番号 */
    @Column(name = "phone_number")
    private String phoneNumber;

    /** アカウント有効フラグ */
    @Column(name = "is_enabled")
    private boolean enabled;

    /** アカウントロックフラグ */
    @Column(name = "is_locked")
    private boolean locked;

    /** MFA（多要素認証）有効フラグ */
    @Column(name = "mfa_enabled")
    private boolean mfaEnabled;

    /** MFAシークレットキー */
    @Column(name = "mfa_secret")
    private String mfaSecret;

    /** 最終ログイン日時 */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

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
    protected User() {
    }

    /**
     * ユーザーエンティティのコンストラクタ
     * 
     * @param id ユーザーID
     * @param username ユーザー名
     * @param email メールアドレス
     * @param passwordHash パスワードハッシュ
     * @param firstName 名
     * @param lastName 姓
     * @param phoneNumber 電話番号
     * @param enabled アカウント有効フラグ
     * @param locked アカウントロックフラグ
     * @param mfaEnabled MFA有効フラグ
     * @param mfaSecret MFAシークレットキー
     * @param lastLogin 最終ログイン日時
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public User(Long id, String username, String email, String passwordHash,
            String firstName, String lastName, String phoneNumber,
            boolean enabled, boolean locked, boolean mfaEnabled,
            String mfaSecret, LocalDateTime lastLogin,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
        this.locked = locked;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ========== Getters ==========
    
    /**
     * ユーザーIDを取得します
     * 
     * @return ユーザーID
     */
    public Long getId() {
        return id;
    }

    /**
     * ユーザー名を取得します
     * 
     * @return ユーザー名
     */
    public String getUsername() {
        return username;
    }

    /**
     * メールアドレスを取得します
     * 
     * @return メールアドレス
     */
    public String getEmail() {
        return email;
    }

    /**
     * パスワードハッシュを取得します
     * 
     * @return パスワードハッシュ
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * 名を取得します
     * 
     * @return 名
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 姓を取得します
     * 
     * @return 姓
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 電話番号を取得します
     * 
     * @return 電話番号
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * アカウントが有効かどうかを取得します
     * 
     * @return 有効な場合true
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * アカウントがロックされているかどうかを取得します
     * 
     * @return ロックされている場合true
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * MFAが有効かどうかを取得します
     * 
     * @return MFAが有効な場合true
     */
    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    /**
     * MFAシークレットキーを取得します
     * 
     * @return MFAシークレットキー
     */
    public String getMfaSecret() {
        return mfaSecret;
    }

    /**
     * 最終ログイン日時を取得します
     * 
     * @return 最終ログイン日時
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
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
