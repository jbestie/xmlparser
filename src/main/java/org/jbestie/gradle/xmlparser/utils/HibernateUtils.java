package org.jbestie.gradle.xmlparser.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by bestie on 13.02.2017.
 */
public final class HibernateUtils {
    private HibernateUtils(){}

    private static volatile SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtils.class) {
                if (sessionFactory == null) {
                    sessionFactory = new Configuration().configure().buildSessionFactory();
                }
            }
        }

        return sessionFactory;
    }
}
