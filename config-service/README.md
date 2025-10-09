


## Ready-to-Run Spring Boot Project Skeleton: Load Config from DB on Startup 

> In a Spring Boot application, is it possible to load application configuration properties from a database at startup, such that the properties can be used via **@Value** or **@ConfigurationProperties** placeholders, and the application will fail to start if any required property is missing?

> Yes, you can load application configuration properties from a database in a Spring Boot app at startup, but Spring’s native `@Value` or `@ConfigurationProperties` **placeholders cannot be used directly until the properties are available**, and Spring Boot normally resolves those before the `ApplicationContext` is fully initialized.

### If you want to fail startup if any expected config is missing, here’s a robust approach:

---

### 📁 Project Structure
````scss
spring-db-config-demo/
├── src/
│   └── main/
│       ├── java/com/example/demo/
│       │   ├── DemoApplication.java
│       │   ├── config/DatabasePropertyInitializer.java
│       │   └── config/DatabasePropertySource.java
│       └── resources/
│           ├── application.properties
│           └── META-INF/spring.factories
└── pom.xml
````

## 1. Create a custom PropertySource that loads from DB
Implement a `PropertySource` loader early in the Spring lifecycle:
````java
public class DatabasePropertySource extends PropertySource<Object> {

    private final Map<String, Object> properties;

    public DatabasePropertySource(String name, Map<String, Object> properties) {
        super(name);
        this.properties = properties;
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }
}
````

## 2. Register the custom property source early
Use a `ApplicationContextInitializer` to register it before Spring reads configuration:
````java
package com.example.demo.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.*;
import java.util.*;

public class DatabasePropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        try {
            Map<String, Object> props = loadPropertiesFromDb();

            List<String> requiredProps = List.of("my.config.username");
            for (String key : requiredProps) {
                if (!props.containsKey(key)) {
                    throw new IllegalStateException("Missing required property: " + key);
                }
            }

            ConfigurableEnvironment env = context.getEnvironment();
            env.getPropertySources().addFirst(new DatabasePropertySource("databaseProperties", props));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB properties", e);
        }
    }

    /*private Map<String, Object> loadPropertiesFromDb() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement stmt = conn.createStatement();

        // Simulate property table
        stmt.execute("CREATE TABLE IF NOT EXISTS app_config (property_key VARCHAR(100), property_value VARCHAR(100))");
        stmt.execute("INSERT INTO app_config (property_key, property_value) VALUES ('my.config.username', 'admin')");

        ResultSet rs = stmt.executeQuery("SELECT property_key, property_value FROM app_config");
        while (rs.next()) {
            map.put(rs.getString("property_key"), rs.getString("property_value"));
        }

        return map;
    }*/

    // Remove inline SQL from loadPropertiesFromDb():
    private Map<String, Object> loadPropertiesFromDb() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT property_key, property_value FROM app_config")) {

            while (rs.next()) {
                map.put(rs.getString("property_key"), rs.getString("property_value"));
            }
        }
        return map;
    }
}

````
Create `schema.sql` in `src/main/resources`
````sql
CREATE TABLE IF NOT EXISTS app_config (
    property_key VARCHAR(100) PRIMARY KEY,
    property_value VARCHAR(100)
);
````
Create data.sql in `src/main/resources`:
````sql
INSERT INTO app_config (property_key, property_value) VALUES ('my.config.username', 'admin');
````

## 📝 application.properties
````properties
# Keep it minimal
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
````

## 3. Register the initializer in `application.properties` or `spring.factories`
If using `application.properties`:
````properties
context.initializer.classes=com.example.DatabasePropertyInitializer
````
Or in `META-INF/spring.factories`:
````properties
org.springframework.context.ApplicationContextInitializer=com.example.DatabasePropertyInitializer
````




## ✅ Usage in Code
💻 DemoApplication.java
````java
package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

    @Value("${my.config.username}")
    private String username;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    public void printConfig() {
        System.out.println("Loaded config username = " + username);
    }
}

````


## 🛑 Important Notes
- You must use raw JDBC or similar in the initializer—JPA/Hibernate beans won’t be initialized yet.
- This is different from `@PostConstruct` or `CommandLineRunner`—those run too late to enforce config presence.
- You can also optionally cache the config later in a `@Component`.

---

