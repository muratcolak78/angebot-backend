package com.angebot.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settings")
@Setter
@Getter
@ToString
public class Settings {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    private String companyName;
    private String phone;
    private String email;
    private String taxNumber;

    private String street;       // "Bahnhofstraße"
    @Column(name = "house_nr")
    private String houseNr;  // "12"
    private String plz;          // "70173"
    private String ort;          // "S

    private UUID logoAssetId;
    private UUID signatureAssetId;

    @Transient
    public String getFullAddress() {
        return String.format("%s %s, %s %s", street, houseNr, plz, ort);
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt = Instant.now();

}