package com.trustai.common.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String mobile;
    private String username;

    private boolean emailVerified;
    private boolean mobileVerified;
    private boolean usernameChecked;

    private boolean registrationCompleted;

    private String ipAddress;
    private String userAgent;
    private String referrer;;
    private String country;
    private String city;

    private String deviceType;
    private String deviceOs;
    private String deviceBrowser;

    @Lob
    @Column(name = "ip_details_json", columnDefinition = "TEXT")
    //@Column(name = "ip_details_json", columnDefinition = "jsonb")// for PostgreSQL
    private String ipDetailsJson;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastUpdated;

    // Optional: IP address, referral code, etc.

}
