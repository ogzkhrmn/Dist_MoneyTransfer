package com.bank.accounting.dao.impl;

import com.bank.accounting.core.annotation.ServiceBean;
import com.bank.accounting.dao.AbstractDao;
import com.bank.accounting.dao.DenemeDao;
import com.bank.accounting.entities.Deneme;

@ServiceBean("denemeDao")
public class DenemeDaoImpl extends AbstractDao implements DenemeDao {

    
    public void save(String name){
        Deneme deneme = new Deneme();
        deneme.setAlan(name);
        getSessionFactory().save(deneme);
    }

}
