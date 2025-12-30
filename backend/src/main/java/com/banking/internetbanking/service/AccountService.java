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

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account createAccount(Long userId, String accountType, String currency, BigDecimal interestRate) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(
                null, userId, accountNumber, accountType,
                BigDecimal.ZERO, currency, "ACTIVE", interestRate,
                LocalDateTime.now(), LocalDateTime.now());
        return accountRepository.save(account);
    }

    public boolean updateAccount(Account account) {
        return accountRepository.save(account) != null;
    }

    public boolean deleteAccount(Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            accountRepository.delete(account.get());
            return true;
        }
        return false;
    }

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

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
