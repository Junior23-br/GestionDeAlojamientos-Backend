package com.gestion.alojamientos.models;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "booking_id", length = 36, nullable = false)
    private String booking_id;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String payment_method;

    @Column(name = "payment_status", length = 20, nullable = false)
    private String payment_status;

    @Column(name = "amount", precision = 10, nullable = false)
    private double amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBooking_id() { return booking_id; }
    public void setBooking_id(String booking_id) { this.booking_id = booking_id; }

    public String getPayment_method() { return payment_method; }
    public void setPayment_method(String payment_method) { this.payment_method = payment_method; }

    public String getPayment_status() { return payment_status; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }
}
