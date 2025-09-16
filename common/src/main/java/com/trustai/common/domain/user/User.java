package com.trustai.common.domain.user;


import com.trustai.common.utils.IdConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String accountId;
    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    private String mobile;
    private String image;
    private int point = 100;
    private String walletAddress;

    // Balance Related....................
    @Column(name = "wallet_balance", precision = 19, scale = 4)
    private BigDecimal walletBalance = BigDecimal.ZERO;
    @Column(name = "profit_balance", precision = 19, scale = 4)
    private BigDecimal profitBalance = BigDecimal.ZERO;
//    @Column(name = "deposit_balance", precision = 19, scale = 4)
//    private BigDecimal depositBalance = BigDecimal.ZERO;

    // Referral & User Hierarchy Related..................
    @Column(name = "referral_code", length = 10)
    //@Setter(AccessLevel.NONE)
    private String referralCode;

    @ManyToOne
    @JoinColumn(name = "referrer_id", referencedColumnName = "id")
    private User referrer;

    @Column(name = "rank_code", nullable = true)
    private String rankCode = "RANK_0";

    // KycInfo..................
//    @OneToOne(optional = false, cascade = CascadeType.ALL) // Makes the association required (not null)
//    @JoinColumn(name = "kyc_info", nullable = false) // Maps to the actual foreign key column
//    private Kyc kycInfo;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TransactionStatus depositStatus = TransactionStatus.DISABLED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TransactionStatus withdrawStatus = TransactionStatus.DISABLED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TransactionStatus sendMoneyStatus = TransactionStatus.DISABLED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean emailVerified;
    private boolean mobileVerified;
    private String oauthProvider; // e.g., "GOOGLE", "GITHUB"
    private String oauthId;


    //@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles = new HashSet<>();


    public String getCountry() {
        return "India";
    }



    /*
    private String mobile;

    private int referBy;

    private int posId;
    private int position;
    private int planId;
    private float totalInvest;
    private float totalBinaryCom;
    private float totalRefCom;
    private int dailyAdLimit;

    private Address addressDetails;

    private int status;

    private String KycStatus;
    private int profileComplete;
    private String banReason;

    private String rememberToken;
    private String provider;
    private int providerId;*/


    @PrePersist
    private void prePersistRef() {
        if (this.getCreatedAt() == null) this.setCreatedAt(LocalDateTime.now());
    }

    @PostPersist
    private void setReferralAfterInsert() {
        this.setAccountId(IdConverter.encode(this.id));
        this.referralCode = "REF" + this.id;
    }

    public User(String username) {
        this.username = username;
        this.setAccountStatus(AccountStatus.PENDING);
    }

    public User(String username, String rankCode, BigDecimal walletBalance) {
        this(username);
        this.rankCode = rankCode;
        this.walletBalance = walletBalance;
        this.setAccountStatus(AccountStatus.PENDING);
    }

    public User(Long id, String username, String rankCode, BigDecimal walletBalance) {
        this(username, rankCode, walletBalance);
        this.id = id;
    }

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    public String getFullName() {
        if (firstname != null && lastname != null) {
            return firstname + " " + lastname;
        } else if (firstname != null) {
            return firstname;
        } else if (lastname != null) {
            return lastname;
        } else {
            return username;
        }
    }

    /*
                    --> DISABLED
                   |
           PENDING  --> ACTIVE  ---> SUSPENDED / BANNED / LOCKED
                   |
                    --> DISABLED

     */
    public enum AccountStatus {
        ACTIVE,         // User is active and allowed to use the system
        DISABLED,       // User is deactivated (manually or due to violation)
        PENDING,        // User registered but hasn't completed verification
        SUSPENDED,      // Temporarily banned for a specific reason/time
        DELETED,        // Soft-deleted user (can be restored later)
        BANNED,         // Permanently banned
        LOCKED,         // Account locked due to security reasons (e.g., too many login attempts)
        INACTIVE        // User hasn’t used the service in a long time
    }

    public enum TransactionStatus {
        ENABLED,
        DISABLED,
    }

    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(new Role(role));
    }
}