
````css
trustai-api/
│
├── foundation-core
│    ├── domain/         # Common entities, enums, DTOs
│    ├── security/       # Spring Security config, filters, JWT utils
│    ├── exceptions/     # Global exception handling
│    ├── utils/          # Shared utility classes
│    ├── config/         # Common Spring configs
│    └── pom.xml
│
├── user-service
│    └── depends on foundation-core
│
├── transaction-service
│    └── depends on foundation-core
│
├── storage-service
│    └── depends on foundation-core
│
├── mlm-rank-service
│    └── depends on foundation-core
│
├── investment-service
│    └── depends on foundation-core
│
├── income-service
│    └── depends on foundation-core
│
└── nft-core (aggregator)
     └── includes all above modules

````

````css
           foundation-core
                ↑
     ┌──────────┼───────────┬───────────┬────────────┬────────────┬────────────┐
user-service   transaction  storage   mlm-rank   investment   income

````

````css
foundation-core
│
├── src/main/java/com/mlm/foundationcore
│   │
│   ├── config/
│   │   ├── SecurityConfig.java      # Spring Security setup
│   │   ├── WebConfig.java           # Common web configs (CORS, interceptors)
│   │   └── AppProperties.java       # Common properties binding
│   │
│   ├── domain/
│   │   ├── user/
│   │   │   ├── User.java            # JPA entity
│   │   │   ├── Role.java            # Enum or entity
│   │   │   └── Permission.java      # If using fine-grained permissions
│   │   ├── common/
│   │   │   ├── BaseEntity.java      # audit fields, IDs
│   │   │   └── Status.java          # e.g. ACTIVE/INACTIVE
│   │
│   ├── repository/
│   │   ├── user/
│   │   │   └── UserRepository.java
│   │   └── common/
│   │       └── BaseRepository.java  # if you use a custom base repo
│   │
│   ├── security/
│   │   ├── jwt/
│   │   │   ├── JwtProvider.java
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── service/
│   │   │   ├── CustomUserDetails.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── AuthEntryPoint.java
│   │   └── SecurityUtils.java
│   │
│   ├── exception/
│   │   ├── BusinessException.java
│   │   ├── NotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   │
│   └── util/
│       ├── DateUtils.java
│       └── StringUtils.java
│
└── pom.xml

````