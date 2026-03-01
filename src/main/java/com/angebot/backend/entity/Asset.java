package com.angebot.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;


@Entity @Table(name = "assets")
@Data  // Lombok
public class Asset {
    @Id @GeneratedValue private UUID id;              // AUTOMATIC SON
    @Column(name = "contenttype", nullable = false)
    private String contentType;                       // 1
    @Column(name = "createdat", nullable = false)
    private Instant createdAt = Instant.now();        // 2
    @Lob @Column(name = "data", nullable = false)
    private byte[] data;                              // 3
    @Column(name = "filename", nullable = false)
    private String fileName;                          // 4
}


