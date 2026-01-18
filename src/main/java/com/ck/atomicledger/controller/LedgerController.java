package com.ck.atomicledger.controller;

import com.ck.atomicledger.core.Wallet;
import com.ck.atomicledger.repository.IWalletRepository;
import com.ck.atomicledger.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final IWalletRepository walletRepository;
    private final TransferService transferService; // Inject Service

    @PostMapping("/init/{userId}")
    public Wallet createWallet(@PathVariable Long userId) {
        // Initialize with 1000 balance for testing
        return walletRepository.save(new Wallet(userId, BigDecimal.valueOf(1000), 0L));
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromId,
                           @RequestParam Long toId,
                           @RequestParam BigDecimal amount) {
        transferService.transfer(fromId, toId, amount);
        return "Transfer Initiated";
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId) {
        return walletRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}
