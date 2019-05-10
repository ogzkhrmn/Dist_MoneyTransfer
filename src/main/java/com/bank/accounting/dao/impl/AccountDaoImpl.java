package com.bank.accounting.dao.impl;

import com.bank.accounting.core.annotation.RealTransaction;
import com.bank.accounting.core.annotation.ServiceBean;
import com.bank.accounting.dao.AbstractDao;
import com.bank.accounting.dao.AccountDao;
import com.bank.accounting.entities.Account;
import com.bank.accounting.entities.ErrorEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;

@ServiceBean("accountDao")
public class AccountDaoImpl extends AbstractDao implements AccountDao {

    @RealTransaction
    @Override
    public boolean hasUserLimit(String tckn, BigDecimal amount) {
        Session session = getSessionFactory();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Account> query = builder.createQuery(Account.class);
        Root<Account> root = query.from(Account.class);
        query.select(root).where(builder.equal(root.get("tckn"), tckn));
        Account table = session.createQuery(query).setMaxResults(1).getSingleResult();
        return table.getBalance().compareTo(amount) >= 0;
    }

    @RealTransaction
    @Override
    public void saveError(ErrorEntity errorEntity) {
        getSessionFactory().saveOrUpdate(errorEntity);
    }

    @RealTransaction
    @Override
    public ErrorEntity getErrorEntity(Long id) {
        return getSessionFactory().get(ErrorEntity.class, id);
    }

}
