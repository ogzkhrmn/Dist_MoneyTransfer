package com.bank.accounting.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "error_account")
public class ErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "json")
    private String json;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "error_tcmb")
    private Boolean errorTcmb = false;

    @Column(name = "error_security")
    private Boolean errorSecurity = false;

    @Column(name = "count")
    private Integer count;

    public Long getId() {
        return id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getErrorTcmb() {
        return errorTcmb;
    }

    public void setErrorTcmb(Boolean errorTcmb) {
        this.errorTcmb = errorTcmb;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getErrorSecurity() {
        return errorSecurity;
    }

    public void setErrorSecurity(Boolean errorSecurity) {
        this.errorSecurity = errorSecurity;
    }
}
