package com.wigerlabs.wikka_lk.service;

import com.wigerlabs.wikka_lk.entity.Status;
import com.wigerlabs.wikka_lk.entity.UserRole;
import com.wigerlabs.wikka_lk.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class DataInitializationService {

    public static void initializeDefaultData() {
        initializeUserRoles();
        initializeStatuses();
    }

    private static void initializeUserRoles() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Check if roles already exist
            List<UserRole> existingRoles = session.createQuery("FROM UserRole", UserRole.class).list();

            if (existingRoles.isEmpty()) {
                // Create default roles using native SQL to ensure specific IDs
                LocalDateTime now = LocalDateTime.now();

                session.createNativeMutationQuery("INSERT INTO user_role (id, name, created_at, updated_at) VALUES (:id1, :name1, :created1, :updated1)")
                        .setParameter("id1", 1)
                        .setParameter("name1", "admin")
                        .setParameter("created1", now)
                        .setParameter("updated1", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO user_role (id, name, created_at, updated_at) VALUES (:id2, :name2, :created2, :updated2)")
                        .setParameter("id2", 2)
                        .setParameter("name2", "seller")
                        .setParameter("created2", now)
                        .setParameter("updated2", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO user_role (id, name, created_at, updated_at) VALUES (:id3, :name3, :created3, :updated3)")
                        .setParameter("id3", 3)
                        .setParameter("name3", "buyer")
                        .setParameter("created3", now)
                        .setParameter("updated3", now)
                        .executeUpdate();

                transaction.commit();
                System.out.println("Default user roles initialized successfully.");
            } else {
                System.out.println("User roles already exist. Skipping initialization.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error initializing user roles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static void initializeStatuses() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Check if statuses already exist
            List<Status> existingStatuses = session.createQuery("FROM Status", Status.class).list();

            if (existingStatuses.isEmpty()) {
                // Create default statuses using native SQL
                LocalDateTime now = LocalDateTime.now();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 1)
                        .setParameter("value", Status.Type.ACTIVE.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 2)
                        .setParameter("value", Status.Type.INACTIVE.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 3)
                        .setParameter("value", Status.Type.PENDING.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 4)
                        .setParameter("value", Status.Type.BLOCKED.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 5)
                        .setParameter("value", Status.Type.VERIFIED.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 6)
                        .setParameter("value", Status.Type.CANCELED.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                session.createNativeMutationQuery("INSERT INTO status (id, value, created_at, updated_at) VALUES (:id, :value, :created, :updated)")
                        .setParameter("id", 7)
                        .setParameter("value", Status.Type.COMPLETED.getValue())
                        .setParameter("created", now)
                        .setParameter("updated", now)
                        .executeUpdate();

                transaction.commit();
                System.out.println("Default status values initialized successfully.");
            } else {
                System.out.println("Status values already exist. Skipping initialization.");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error initializing status values: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

