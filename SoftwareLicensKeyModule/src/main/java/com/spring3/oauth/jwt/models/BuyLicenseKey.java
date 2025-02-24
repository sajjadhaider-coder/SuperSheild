package com.spring3.oauth.jwt.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BuyLicenseKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    private long userId;
    private long softwareId;
    private long softwareLicenseId;
    private long price;
    private LocalDateTime purchaseDate;
    private String Duration; // MONTHLY, QUARTARLY, ANUALLY
    private LocalDateTime expirydate;

}
