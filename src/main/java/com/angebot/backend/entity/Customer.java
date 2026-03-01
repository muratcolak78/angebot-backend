package com.angebot.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter @Setter
public class Customer {
    @Id @GeneratedValue private UUID id;
    
    @Column(nullable = false, name = "user_id")  // ← RateCard gibi!
    private UUID userId;
    
    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    @Column() private String email;
    @Column() private String phone;

    //  Addresse
    @Column(name = "home_street") private String homeStreet;
    @Column(name = "home_house_nr") private String homeHouseNr;
    @Column(name = "home_plz") private String homePlz;
    @Column(name = "home_ort") private String homeOrt;

    // Adresse segmentiert (Arbeit - optional)
    @Column(name = "work_street") private String workStreet;
    @Column(name = "work_house_nr") private String workHouseNr;
    @Column(name = "work_plz") private String workPlz;
    @Column(name = "work_ort") private String workOrt;

    // Helper methods
    @Transient
    public String getHomeAddress() {
        return String.format("%s %s, %s %s", homeStreet, homeHouseNr, homePlz, homeOrt);
    }

    @Transient
    public String getWorkAddress() {
        if (workStreet == null) return "";
        return String.format("%s %s, %s %s", workStreet, workHouseNr, workPlz, workOrt);
    }

    @Column(nullable = false, name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at")  // ← Update timestamp
    private Instant updatedAt;
}
