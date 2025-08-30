package com.trustai.userservice.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_details")
@Data
@NoArgsConstructor
public class Kyc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;

    private String email;
    private String phone;
    private String address;
    private String birthdate;
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycDocumentType documentType = KycDocumentType.NATIONAL_ID;
    private String identityNumber;
    private String documentImage;

    // ########################### Status #############################
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EpaStatus emailVerifyStatus = EpaStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EpaStatus phoneVerifyStatus = EpaStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EpaStatus addressVerification = EpaStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public KycStatus status = KycStatus.PENDING;
    // ########################### Status #############################


    private String kycRejectionReason;
    private Long approver;


    @Column(nullable = false, updatable = true)
    private LocalDateTime createdAt;



    public enum EpaStatus {
        UNVERIFIED,
        VERIFIED,
    }

    public enum KycStatus {
        PENDING,        // Not yet submitted the KYC Documents
        UNVERIFIED,     // KYC Document Submitted, but not verified from Admin
        VERIFIED,       // Documents Verified by Admin
        REJECTED;       // KYC Rejected
    }

    public enum KycDocumentType {
        AADHAR,
        PAN,
        PASSPORT,
        DRIVING_LICENSE,
        NATIONAL_ID,
        VOTER_ID,
        TAX_ID,
        SSN;  // For international cases like the US
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
