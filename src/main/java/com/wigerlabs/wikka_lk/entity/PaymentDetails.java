package com.wigerlabs.wikka_lk.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_details")
public class PaymentDetails extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "payment_method", nullable = false, length = 100)
    private String paymentMethod;

    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
