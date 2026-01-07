package com.wigerlabs.wikka_lk;

import com.wigerlabs.wikka_lk.config.AppConfig;
import com.wigerlabs.wikka_lk.service.DataInitializationService;
import com.wigerlabs.wikka_lk.listener.ContextPathListener;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.SessionFactory;

import java.io.File;

public class Main {

    private static final int SERVER_PORT = 8080;
    private static final String CONTEXT_PATH = "/wikka_lk";

    public static void main(String[] args) {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(SERVER_PORT);
            tomcat.getConnector();

            Context context = tomcat.addWebapp(CONTEXT_PATH, new File("src/main/webapp").getAbsolutePath());
            Tomcat.addServlet(context, "JerseyServlet", new ServletContainer(new AppConfig()));
            context.addServletMappingDecoded("/api/*", "JerseyServlet");

            context.addApplicationListener(ContextPathListener.class.getName());

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
