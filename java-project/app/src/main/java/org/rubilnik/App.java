package org.rubilnik;

import org.rubilnik.basicLogic.users.User;
import org.rubilnik.dependencyClasses.Strings;
import org.rubilnik.webAPI.HTTP_Controller;
import org.springframework.boot.SpringApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// main application class
public class App {
    static Strings strings;
    public String getGreeting(){
        return "Hello world";
    }

    static{
        var context = new ClassPathXmlApplicationContext("dependencies.xml");
        strings = context.getBean("Strings", Strings.class);
        context.close();
    }
    public static Strings getStrings() {
        return strings;
    }
    public static void main(String[] args) {
        // get and check existing id's from database
        var db_session = Database.getSessionFactory().openSession();
        var db_transaction = db_session.beginTransaction();
        var db_query = db_session.createNativeQuery("select * from "+Database.getTableName(User.class), User.class);
        var db_users = db_query.list();
        try{
            db_users.forEach((user)->{
                User.getIdManager().putId(user.getId());
            });
        } catch (IllegalArgumentException e){
            throw new RuntimeException("In database table "+Database.getTableName(User.class)+"\n"+e.getMessage());
        }
        db_transaction.commit();
        db_session.close();
        User.getIdManager().log();

        // create and persist def users if dont exist
        db_session = Database.getSessionFactory().openSession();
        db_transaction = db_session.beginTransaction();
        db_query = db_session.createNativeQuery("select * from "+Database.getTableName(User.class)+" where "+"email='"+System.getenv("RUBILNIK_DEF_USR_EMAIL")+"'", User.class);
        var db_user = db_query.uniqueResult();
        if (db_user==null) {
            var def = new User(System.getenv("RUBILNIK_DEF_USR_NAME"),System.getenv("RUBILNIK_DEF_USR_EMAIL"), System.getenv("RUBILNIK_DEF_USR_PASSWORD"));
            db_session.persist(def);
        }
        db_transaction.commit();
        db_session.close();

        // Run SpringBoot App
        SpringApplication.run(HTTP_Controller.class, args);
    }
}