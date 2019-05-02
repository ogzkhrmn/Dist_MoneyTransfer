package com.bank.accounting.core.handler;

import com.bank.accounting.core.HibernateConfiguration;
import com.bank.accounting.dao.impl.DenemeDaoImpl;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicInvocationHandler implements InvocationHandler {

    private Object obj;

    public DynamicInvocationHandler(Object obj) {
        this.obj = obj;
    }

    @Transactional
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Session session = HibernateConfiguration.getSession().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        method.invoke(obj, args);
        transaction.commit();
        return null;
    }
}
