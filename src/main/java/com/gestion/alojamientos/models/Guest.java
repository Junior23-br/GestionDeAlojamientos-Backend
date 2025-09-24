package com.gestion.alojamientos.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

public class Guest extends User {
    private String userId;
    private JsonNode preferences;
    private double rating;
    private JsonNode emergencyContact;
    private int totalBookings;
    private LocalDateTime lastBookingAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public JsonNode getPreferences() {
        return preferences;
    }

    public void setPreferences(JsonNode preferences) {
        this.preferences = preferences;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public JsonNode getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(JsonNode emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public LocalDateTime getLastBookingAt() {
        return lastBookingAt;
    }

    public void setLastBookingAt(LocalDateTime lastBookingAt) {
        this.lastBookingAt = lastBookingAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
