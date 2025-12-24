package com.wigerlabs.wikka_lk;

import com.wigerlabs.wikka_lk.config.AppConfig;
import com.wigerlabs.wikka_lk.service.DataInitializationService;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.SessionFactory;

public class Main {

    private static final int SERVER_PORT = 8080;
    private static final String CONTEXT_PATH = "/wikka_lk";

    public static void main(String[] args) {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(SERVER_PORT);
            tomcat.getConnector();

            Context context = tomcat.addContext(CONTEXT_PATH, null);
            Tomcat.addServlet(context, "JerseyServlet", new ServletContainer(new AppConfig()));
            context.addServletMappingDecoded("/api/*", "JerseyServlet");

//            context.addApplicationListener(ContextPathListener.class.getName());

            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

            // Initialize default data (user roles, etc.)
            DataInitializationService.initializeDefaultData();

            tomcat.start();
            System.out.println("App URL: http://localhost:" + SERVER_PORT + CONTEXT_PATH);
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException("Tomcat Embedded Server loading failed: " + e.getMessage());
        }
    }
}
