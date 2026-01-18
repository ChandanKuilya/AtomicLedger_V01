package com.ck.atomicledger.repository;

import com.ck.atomicledger.core.Wallet;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IWalletRepository extends CrudRepository<Wallet, Long> {

    // The magic words: FOR UPDATE
    // This forces MySQL to hold a row lock on this specific record
    @Query("SELECT * FROM wallets WHERE user_id = :id FOR UPDATE")
    Optional<Wallet> findByIdForUpdate(@Param("id") Long id);
}