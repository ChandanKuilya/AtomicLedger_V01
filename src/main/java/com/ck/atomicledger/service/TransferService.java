package com.ck.atomicledger.service;

import com.ck.atomicledger.core.Wallet;
import com.ck.atomicledger.repository.IWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final IWalletRepository walletRepository;

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // 1. DEADLOCK PREVENTION: Always lock smaller ID first
        Long firstLock = fromId < toId ? fromId : toId;
        Long secondLock = fromId < toId ? toId : fromId;

        // 2. ACQUIRE LOCKS (Pessimistic)
        // We trigger the lock on the first wallet, then the second.
        // Other threads trying to lock these same rows will WAIT here.
        Wallet w1 = walletRepository.findByIdForUpdate(firstLock)
                .orElseThrow(() -> new RuntimeException("Wallet " + firstLock + " not found"));

        Wallet w2 = walletRepository.findByIdForUpdate(secondLock)
                .orElseThrow(() -> new RuntimeException("Wallet " + secondLock + " not found"));

        // 3. RE-ASSIGN to sender/receiver correctly
        Wallet sender = (fromId.equals(firstLock)) ? w1 : w2;
        Wallet receiver = (fromId.equals(firstLock)) ? w2 : w1;

        // 4. BUSINESS LOGIC (Now Thread-Safe)
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);
    }
}
