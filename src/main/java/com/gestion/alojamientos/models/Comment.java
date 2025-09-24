package com.gestion.alojamientos.models;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "author_id", length = 36, nullable = false)
    private String author_id;

    @Column(name = "target_user_id", length = 36, nullable = false)
    private String target_user_id;

    @Column(name = "rentalhouse_id", length = 36, nullable = false)
    private String rentalhouse_id;

    @Column(name = "booking_id", length = 36, nullable = false)
    private String booking_id;

    @Column(name = "rating", precision = 2, nullable = false)
    private double rating;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_flagged", nullable = false)
    private boolean is_flagged;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updated_at;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthor_id() { return author_id; }
    public void setAuthor_id(String author_id) { this.author_id = author_id; }

    public String getTarget_user_id() { return target_user_id; }
    public void setTarget_user_id(String target_user_id) { this.target_user_id = target_user_id; }

    public String getRentalhouse_id() { return rentalhouse_id; }
    public void setRentalhouse_id(String rentalhouse_id) { this.rentalhouse_id = rentalhouse_id; }

    public String getBooking_id() { return booking_id; }
    public void setBooking_id(String booking_id) { this.booking_id = booking_id; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isIs_flagged() { return is_flagged; }
    public void setIs_flagged(boolean is_flagged) { this.is_flagged = is_flagged; }

    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }
}
