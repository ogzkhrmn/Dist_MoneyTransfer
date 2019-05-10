package com.bank.accounting.dao;

import com.bank.accounting.core.annotation.RealTransaction;
import com.bank.accounting.entities.ErrorEntity;

import java.math.BigDecimal;

public interface AccountDao {

    boolean hasUserLimit(String tckn, BigDecimal amount);

    @RealTransaction
    void saveError(ErrorEntity errorEntity);

    @RealTransaction
    ErrorEntity getErrorEntity(Long id);
}
