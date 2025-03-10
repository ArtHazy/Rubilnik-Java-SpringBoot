package org.rubilnik;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.rubilnik.basicLogic.Quiz;
import org.rubilnik.basicLogic.Quiz.Question;
import org.rubilnik.basicLogic.Quiz.Question.Choice;
import org.rubilnik.basicLogic.users.User;

import jakarta.persistence.Table;

public class Database {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // configure database structure with annotated classes
    static {
        var config = new Configuration();

        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Quiz.class);
        config.addAnnotatedClass(Question.class);
        config.addAnnotatedClass(Choice.class);

        config.setProperty("hibernate.connection.url", System.getenv("SPRING_DATASOURCE_URL"));
        config.setProperty("hibernate.connection.username", System.getenv("SPRING_DATASOURCE_USERNAME"));
        config.setProperty("hibernate.connection.password", System.getenv("SPRING_DATASOURCE_PASSWORD"));

        config.setProperty("hibernate.connection.pool_size", System.getenv("SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE"));
        config.setProperty("hibernate.hbm2ddl.auto", System.getenv("SPRING_JPA_HIBERNATE_DDL_AUTO"));
        config.setProperty("hibernate.show_sql", System.getenv("SPRING_JPA_SHOW_SQL"));

        sessionFactory = config.buildSessionFactory();
    }
    
    // get default or annotated database table name (for sql queries)
    public static <T> String getTableName(Class<T> c){
        var annotation = c.getAnnotation(Table.class);
        if (annotation != null) {
            return annotation.name();
        } else { 
            return c.getSimpleName();
        }
    }
}
