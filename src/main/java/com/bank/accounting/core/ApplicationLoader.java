package com.bank.accounting.core;

import com.bank.accounting.core.annotation.Bean;
import com.bank.accounting.core.annotation.ServiceBean;
import com.bank.accounting.core.handler.DynamicInvocationHandler;
import com.bank.accounting.core.model.AnnotatedFieldModel;
import com.bank.accounting.core.util.BeanUtil;
import com.bank.accounting.core.util.PackageScanner;
import com.bank.accounting.dao.DenemeDao;
import com.bank.accounting.dao.impl.DenemeDaoImpl;
import com.bank.accounting.service.MoneyTransferService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

class ApplicationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateConfiguration.class);

    static void loadApp() {
        try {
            List<Class> classList = PackageScanner.getClasses(ApplicationProperties.getProperty("app.package"));
            classList.forEach(item -> {
                Annotation[] t = item.getAnnotationsByType(ServiceBean.class);
                if (ArrayUtils.isNotEmpty(t)) {
                    ServiceBean bean = (ServiceBean) t[0];
                    if (StringUtils.isEmpty(bean.value())) {
                        BeanUtil.addBean(item.getSimpleName(), item);
                    } else {
                        BeanUtil.addBean(bean.value(), item);
                    }
                }
                for (Field field : item.getDeclaredFields()) {
                    Annotation[] a = field.getAnnotationsByType(Bean.class);
                    if (ArrayUtils.isNotEmpty(a)) {
                        try {
                            BeanUtil.addField(field.getName(), new AnnotatedFieldModel(item, field));
                        } catch (Exception e) {
                            LOGGER.error("Creatiton error ", e);
                        }
                    }
                }
                item.getFields();
            });
        } catch (Exception e) {
            LOGGER.error("Package Reading problem ", e);
        }
    }

    static void addPrxoy() {
        try {
            BeanUtil.getFields().keySet().forEach(item -> {
                AnnotatedFieldModel annotatedFieldModel = BeanUtil.getField(item);
                Class clazz = BeanUtil.getBean(item);
                try {
                    Object object = Proxy.newProxyInstance(clazz.getClassLoader(),
                            new Class[]{annotatedFieldModel.getField().getType()},
                            new DynamicInvocationHandler(clazz.newInstance()));
                    annotatedFieldModel.getField().setAccessible(true);
                    Field f = annotatedFieldModel.getClazz().getDeclaredField(item);
                    f.setAccessible(true);
                    f.set(null, object);
                } catch (Exception e) {
                    LOGGER.error("Bean creation error", e);
                }
            });
        } catch (Exception e) {
            LOGGER.error("Package Reading problem ", e);
        }
    }

}
