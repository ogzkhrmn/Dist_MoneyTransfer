package com.bank.accounting.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseModel implements Serializable {

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    private String field;

}
