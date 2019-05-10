package com.bank.accounting.model;

import java.io.Serializable;

public class AccountErrorRequest implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
