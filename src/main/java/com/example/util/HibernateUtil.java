package com.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Для тестов используем специальный конфиг
                if (isTestEnvironment()) {
                    configuration.configure("hibernate-test.cfg.xml");
                } else {
                    configuration.configure("hibernate.cfg.xml");
                }

                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }

    private static boolean isTestEnvironment() {
        return System.getProperty("test.env") != null
                || System.getProperty("hibernate.connection.url") != null;
    }

    public static synchronized void setSessionFactory(SessionFactory sessionFactory) {
        HibernateUtil.sessionFactory = sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}