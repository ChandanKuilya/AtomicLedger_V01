package com.ck.atomicledger.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;



import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("wallets")
public class Wallet implements Persistable<Long> { // 1. Implement Persistable

    @Id
    private Long userId;
    private BigDecimal balance;

    @Version
    private Long version;

    // 2. Add this Transient field (not stored in DB)
    @Transient
    private boolean isNew = false;

    // 3. Custom constructor to create a NEW wallet easily
    public Wallet(Long userId, BigDecimal balance, Long version) {
        this.userId = userId;
        this.balance = balance;
        this.version = version;
        this.isNew = true; // Mark as new so Spring does an INSERT
    }

    // 4. Override getId()
    @Override
    public Long getId() {
        return userId;
    }

    // 5. Override isNew() logic
    @Override
    public boolean isNew() {
        return isNew || userId == null;
    }
}