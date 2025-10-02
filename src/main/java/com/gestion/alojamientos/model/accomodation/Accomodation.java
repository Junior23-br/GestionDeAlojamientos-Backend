package com.gestion.alojamientos.model.accomodation;

import java.time.LocalDateTime;
import java.util.List;

import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.users.Host;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accomodations")
public class Accomodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Unique identifier

    @Column(name = "title", nullable = false, length = 255)
    private String title; // Accommodation title

    @Enumerated(EnumType.STRING)
    @Column(name = "accomodation_type", nullable = false)
    private AccomodationType accomodationType; // Accommodation type

    @Column(name = "house_rules", columnDefinition = "TEXT")
    private String houseRules; // House rules

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ubication_id")
    private Ubication ubication; // Accommodation location

    @Column(name = "max_guest_capacity")
    private Integer maxGuestCapacity; // Maximum guest capacity

    @Column(name = "number_of_beds")
    private Integer numberOfBeds; // Number of beds

    @Column(name = "number_of_bathrooms")
    private Integer numberOfBathrooms; // Number of bathrooms

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus; // Approval status

    @Enumerated(EnumType.STRING)
    @Column(name = "operational_status")
    private OperationalStatus operationalStatus; // Operational status

    @Column(name = "created_time")
    private LocalDateTime createdTime; // Creation date and time

    @Column(name = "update_time")
    private LocalDateTime updateTime; // Update date and time

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host; // Accommodation host

    @OneToMany(mappedBy = "accomodation")
    private List<Booking> bookingList; // List of bookings

    @OneToMany(mappedBy = "accomodation")
    private List<CommentAccomodation> commentary; // List of comments

    @ElementCollection
    @CollectionTable(name = "accomodation_photos", joinColumns = @JoinColumn(name = "accomodation_id"))
    @Column(name = "url_photo")
    private List<String> urlPhotos; // Photo URLs

    @OneToMany(mappedBy = "accomodation")
    private List<AccomodationCalification> accomodationCalificationList; // List of accommodation ratings

    @ManyToMany
    @JoinTable(
        name = "accomodation_services",
        joinColumns = @JoinColumn(name = "accomodation_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Services> servicesList; // List of services
}
