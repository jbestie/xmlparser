package org.jbestie.gradle.xmlparser.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Map;

/**
 * Created by bestie on 13.02.2017.
 */
public final class HibernateUtils {
    private HibernateUtils(){}

    private static volatile SessionFactory sessionFactory = null;
    private static Map<String, String> applicationConfiguration = null;

    public static void initializeSessionFactoryConfig(Map<String, String> applicationConfiguration) {
        HibernateUtils.applicationConfiguration = applicationConfiguration;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtils.class) {
                if (applicationConfiguration == null) {
                    throw new IllegalStateException("Method initializeSessionFactoryConfig has to be called before creation the factory");
                }

                if (sessionFactory == null) {
                    Configuration configuration = new Configuration().configure();
                    configuration.setProperty(Environment.URL, applicationConfiguration.get(ApplicationConstants.CONFIG_CONNECTION_URL));
                    configuration.setProperty(Environment.USER, applicationConfiguration.get(ApplicationConstants.CONFIG_CONNECTION_USERNAME));
                    configuration.setProperty(Environment.PASS, applicationConfiguration.get(ApplicationConstants.CONFIG_CONNECTION_PASSWORD));

                    sessionFactory = configuration.buildSessionFactory();
                }
            }
        }

        return sessionFactory;
    }
}
