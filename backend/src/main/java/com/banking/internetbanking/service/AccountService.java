package com.banking.internetbanking.service;

import com.banking.internetbanking.repository.AccountRepository;
import com.banking.internetbanking.entity.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 口座サービスクラス
 * 
 * 口座関連のビジネスロジックを提供します。
 * 口座の作成、検索、更新、削除、残高更新、振込などの操作を行います。
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * コンストラクタ
     * 
     * @param accountRepository 口座リポジトリ
     */
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * すべての口座を取得します
     * 
     * @return 口座リスト
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * IDで口座を取得します
     * 
     * @param id 口座ID
     * @return 口座エンティティ（存在しない場合は空のOptional）
     */
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    /**
     * ユーザーIDで口座リストを取得します
     * 
     * @param userId ユーザーID
     * @return 口座リスト
     */
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * 口座番号で口座を取得します
     * 
     * @param accountNumber 口座番号
     * @return 口座エンティティ（存在しない場合は空のOptional）
     */
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * 新しい口座を作成します
     * 
     * @param userId ユーザーID
     * @param accountType 口座種別
     * @param currency 通貨
     * @param interestRate 金利
     * @return 作成された口座エンティティ
     */
    public Account createAccount(Long userId, String accountType, String currency, BigDecimal interestRate) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(
                null, userId, accountNumber, accountType,
                BigDecimal.ZERO, currency, "ACTIVE", interestRate,
                LocalDateTime.now(), LocalDateTime.now());
        return accountRepository.save(account);
    }

    /**
     * 口座情報を更新します
     * 
     * @param account 更新する口座エンティティ
     * @return 更新成功の場合true
     */
    public boolean updateAccount(Account account) {
        return accountRepository.save(account) != null;
    }

    /**
     * 口座を削除します
     * 
     * @param id 口座ID
     * @return 削除成功の場合true
     */
    public boolean deleteAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            accountRepository.delete(account.get());
            return true;
        }
        return false;
    }

    /**
     * 口座の残高を更新します
     * 
     * @param accountId 口座ID
     * @param newBalance 新しい残高
     * @return 更新成功の場合true
     */
    public boolean updateBalance(Long accountId, BigDecimal newBalance) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent()) {
            Account updatedAccount = new Account(
                    account.get().getId(), account.get().getUserId(),
                    account.get().getAccountNumber(), account.get().getAccountType(),
                    newBalance, account.get().getCurrency(), account.get().getStatus(),
                    account.get().getInterestRate(), account.get().getCreatedAt(), LocalDateTime.now());
            return accountRepository.save(updatedAccount) != null;
        }
        return false;
    }

    /**
     * 口座間で資金を振り込みます
     * 
     * @param fromAccountId 送金元口座ID
     * @param toAccountId 送金先口座ID
     * @param amount 振込金額
     * @return 振込成功の場合true
     */
    public boolean transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Optional<Account> fromAccount = accountRepository.findById(fromAccountId);
        Optional<Account> toAccount = accountRepository.findById(toAccountId);

        if (fromAccount.isPresent() && toAccount.isPresent()) {
            BigDecimal fromBalance = fromAccount.get().getBalance();
            BigDecimal toBalance = toAccount.get().getBalance();

            if (fromBalance.compareTo(amount) >= 0) {
                // 送金元の残高を減らす
                updateBalance(fromAccountId, fromBalance.subtract(amount));
                // 送金先の残高を増やす
                updateBalance(toAccountId, toBalance.add(amount));
                return true;
            }
        }
        return false;
    }

    /**
     * 口座番号を生成します
     * 
     * @return 生成された口座番号
     */
    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
