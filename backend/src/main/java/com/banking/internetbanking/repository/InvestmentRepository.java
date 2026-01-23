package com.banking.internetbanking.repository;

import com.banking.internetbanking.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserId(Long userId);

    List<Investment> findByAccountId(Long accountId);

    List<Investment> findByUserIdAndStatus(Long userId, String status);
}
