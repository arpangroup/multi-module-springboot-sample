## Kickstart Build the Project

````bash
 mvn clean install -DskipTests
````

## Project Structure:
- **Root project** (`pom.xml`): Maven [Aggregator]() project that is aggregating the 4 child modules so they can be built as a group.
- **REST project** (`react-demo-rest/pom.xml`): Spring Boot application that will host the REST services backing the UI. This project produces a JAR file that is included in the final Spring Boot executable JAR file.
- **Web project** (`react-demo-web/pom.xml`): Hosts the HTML, JS, styles and frontend dependencies. This project is built using [Vite]() . Vite is a modern toolchain that allows you to focus on code, not build tools. This project produces a JAR file containing static resources that is included in the final Spring Boot executable JAR file.
- **Parent project** (`react-demo-parent/pom.xml`): Parent POM project for REST, Web, and application modules that manages the dependency versions and common plugins
- **Application project** (`react-demo-app/pom.xml`): Maven JAR project that generates the final deployable JAR file. This project depends on the REST and Web projects


## Technology Stack Overview

### Web:
- **[React]()**: An open-source JavaScript library created by Facebook to build user interfaces. React uses JSX (an extension of JavaScript), which allows component-based JavaScript and HTML markup to be managed as a single unit.  
- **[Vite]()**: Vite is a modern toolchain that allows you to focus on code, not build tools
### Server:
- **[Spring Boot]()**:
### Database:
- **[MySQL]()**
### Build:
- Maven
- npm
### Testing:
- JUnit5
- Vitest


## ✅ Final Project Structure
````scss
nft-backend-api/
│
├── pom.xml             <-- ROOT POM (artifactId: nft-backend-api)
│
├── common/              # Shared config, DTOs, JPA base classes
│
├── user-service/        # User microservice
│
├── product-service/     # Product microservice
│
└── nft-app/             # Aggregator app (combines all services)

````


````scss
nft-backend-api/
├── pom.xml                <-- ROOT POM (artifactId: nft-backend-api)
├── common/
│   └── pom.xml            <-- CHILD module (artifactId: common)
├── user-service/
│   └── pom.xml
├── product-service/
│   └── pom.xml

````


---

## Top-Level Project (Parent POM)
````swift
mlm-platform/
│
├── pom.xml                 # Parent POM
│
├── mlm-core/               # Shared domain logic (BV/PV/rank/commission)
│   ├── pom.xml
│   └── src/main/java/com/example/mlmcore/
│       ├── domain/         # Entities, enums, value objects
│       ├── service/        # PointCalcService, RankEvaluator, etc.
│       ├── rule/           # Rule engine for rank or income
│       ├── dto/            # Shared DTOs
│       ├── event/          # Domain events (InvestmentCompletedEvent, etc.)
│       └── util/           # Shared utils for calculations
│
├── user-service/
│   ├── pom.xml
│   └── src/main/java/com/example/userservice/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       └── entity/
│
├── product-service/
│   ├── pom.xml
│   └── src/main/java/com/example/productservice/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       └── entity/
│
├── investment-service/
│   ├── pom.xml
│   └── src/main/java/com/example/investmentservice/
│       ├── controller/
│       ├── service/
│       ├── listener/       # Emits events to other services
│       └── repository/
│
├── income-service/
│   ├── pom.xml
│   └── src/main/java/com/example/incomeservice/
│       ├── service/
│       ├── controller/
│       ├── repository/
│       └── listener/       # Consumes events from investments
│
├── rank-service/
│   ├── pom.xml
│   └── src/main/java/com/example/rankservice/
│       ├── service/
│       ├── scheduler/
│       ├── controller/
│       └── listener/
│
├── transaction-service/
│   ├── pom.xml
│   └── src/main/java/com/example/transactionservice/
│       ├── wallet/         # Wallet management
│       ├── transaction/    # Debit/credit records
│       ├── controller/
│       └── repository/
│
├── inAppNotification-service/  # (optional)
│   ├── pom.xml
│   └── src/main/java/com/example/notificationservice/
│       ├── email/
│       ├── sms/
│       └── listener/       # Listens to RankUp or Commission events
│
├── job-service/           # (optional centralized job scheduler)
│   ├── pom.xml
│   └── src/main/java/com/example/jobservice/
│       ├── scheduler/
│       └── config/
│
└── report-service/        # (optional exports, CSV, charts)
    ├── pom.xml
    └── src/main/java/com/example/reportservice/
        ├── service/
        ├── export/
        └── controller/

````

## 🧱 Create: mlm-core module
These components contain domain logic, and are reused across services:
### 1. Entities (and DTOs):
- UserHierarchy
- UserPointSummary
- RankConfig
- CommissionRateConfig
- InvestmentPlan
- Any enums (e.g., `RankType`, `IncomeType`)

### 2. Interfaces / Services (core logic, not infrastructure):
- RankEvaluationService
- PointCalculationService
- CommissionDistributionService
- RankRuleEngine, LevelPointAggregator (if any)
- Utility functions for BV/PV/GV calculations

### 3. Repositories (interfaces only) (optional – if you abstract the repo layer)
### 4. Event models (used in event-driven architecture)
- InvestmentCompletedEvent
- CommissionDistributedEvent
- RankChangedEvent


## 📦 Updated Module Map (Recommended)
| Module              | Purpose                                                     |
| ------------------- | ----------------------------------------------------------- |
| **mlm-core**        | Shared MLM domain logic (entities, rank logic, point rules) |
| income-service      | Handles income history, payouts, wallets                    |
| investment-service  | User stakes, maturity, and returns                          |
| rank-service        | Listens to point updates, handles rank evaluation           |
| product-service     | Investment/product catalog, prices, PV/BV definition        |
| transaction-service | Wallet debit/credit, transfer logs                          |
| user-service        | Registration, referral tree, profile management             |


## 🔄 Example of Interaction Flow
1. **User buys plan** → `investment-service` emits event
2. `mlm-core` logic (via `rank-service` and `income-service`) handles:
   - Point calculation
   - Commission propagation
   - Rank reevaluation
3. `mlm-core` defines rules, events, and evaluators for all of the above.


## ✅ Optional/New Modules for Scalability
Here are future-proof modules you might consider:

| Module              | Purpose                                                     |
| ------------------- | ----------------------------------------------------------- |
| **mlm-core**        | Shared MLM domain logic (entities, rank logic, point rules) |
| income-service      | Handles income history, payouts, wallets                    |
| investment-service  | User stakes, maturity, and returns                          |
| rank-service        | Listens to point updates, handles rank evaluation           |
| product-service     | Investment/product catalog, prices, PV/BV definition        |
| transaction-service | Wallet debit/credit, transfer logs                          |
| user-service        | Registration, referral tree, profile management             |

## 🔍 Are You Missing Anything?
Here’s a checklist:

✅ Entity separation
✅ Rank and commission rules decoupled
✅ Hierarchical traversal logic (closure table)
✅ Wallet and transaction abstraction
✅ Event-driven communication
✅ Daily cron-based updates
✅ Versioned investment products (for future proofing)

🔲 Notification module
🔲 Central audit trail
🔲 Reward engine for campaigns/bonuses
🔲 Event bus abstraction (Kafka, etc.)
🔲 User activity tracker
🔲 Dashboard/report service (for admin reporting)

## ✅ Optional Enhancements
- Use Spring Cloud Stream or Kafka for `event`/ package communication.
- Add `common-lib`/ module for shared logging, error handling, etc.
- Central `gateway-service`/ (API gateway if using microservices)
- Central `auth-service`/ (for JWT and access control)


````css
mlm-platform
├── income-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── incomeservice
│                           ├── controller
│                           ├── listener
│                           └── repository
│                           └── service
├── investment-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── investmentservice
│                           ├── controller
│                           ├── listener
│                           ├── repository
│                           └── service
├── job-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── jobservice
│                           ├── config
│                           └── scheduler
├── mlm-core
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── mlmcore
│                           ├── domain
│                           ├── dto
│                           ├── event
│                           ├── rule
│                           ├── service
│                           └── util
├── inAppNotification-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── notificationservice
│                           ├── email
│                           ├── listener
│                           └── sms
├── product-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── productservice
│                           ├── controller
│                           ├── entity
│                           ├── repository
│                           └── service
├── rank-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── rankservice
│                           ├── controller
│                           ├── listener
│                           ├── scheduler
│                           └── service
├── report-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── reportservice
│                           ├── controller
│                           ├── export
│                           └── service
├── transaction-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── transactionservice
│                           ├── controller
│                           ├── repository
│                           ├── transaction
│                           └── wallet
├── user-service
│   └── src
│       └── main
│           └── java
│               └── com
│                   └── example
│                       └── userservice
│                           ├── controller
│                           ├── entity
│                           ├── repository
│                           └── service

````
This structure supports:
- Commission distribution in `income-service`
- Rank evaluation in `rank-service`
- React frontend integration (you can create a separate `frontend`/ folder)
- Scheduled jobs in `job-service`
- Centralized shared logic in `mlm-core`


## Mlm-multi-module-pom
````xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>mlm-platform</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    <modules>
        <module>mlm-core</module>
        <module>user-service</module>
        <module>investment-service</module>
        <module>income-service</module>
        <module>rank-service</module>
        <module>transaction-service</module>
        <module>product-service</module>
        <module>inAppNotification-service</module>
        <module>report-service</module>
        <module>job-service</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <spring.boot.version>3.2.4</spring.boot.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring.boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
````

