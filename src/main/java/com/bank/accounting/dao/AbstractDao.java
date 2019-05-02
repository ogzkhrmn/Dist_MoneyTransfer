package com.bank.accounting.dao;

import com.bank.accounting.core.HibernateConfiguration;
import org.hibernate.Session;

public class AbstractDao {

    protected Session getSessionFactory(){
        return HibernateConfiguration.getSession().getCurrentSession();
    }

}
