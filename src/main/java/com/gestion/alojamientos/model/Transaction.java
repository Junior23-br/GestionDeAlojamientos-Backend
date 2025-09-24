package com.gestion.alojamientos.model;

import java.util.Currency;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "booking_id", length = 36, nullable = false)
    private String bookingId;

    @Column(name = "payment_method", length = 50, nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", length = 20, nullable = false)
    private TransactionStatus paymentStatus;

    @Column(name = "amount", precision = 10, nullable = false)
    private double amount;

    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBooking_id() { return bookingId; }
    public void setBooking_id(String bookingId) { this.bookingId = bookingId; }

    public String getPayment_method() { return paymentMethod; }
    public void setPayment_method(TransactionStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public TransactionStatus getPayment_status() { return paymentStatus; }
    public void setPayment_status(String paymentStatus) { this.paymentMethod = paymentStatus; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }
}
