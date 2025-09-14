//package com.arpangroup.config_service.entity;
//
//import com.arpangroup.config_service.repository.ConfigPropertyRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class ConfigPropertyInitializer {
//    private final ConfigPropertyRepository repository;
//
//    @PostConstruct
//    public void init() {
//        List<ConfigProperty> configs = List.of(
//                // Database
//                new ConfigProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/nft"),
//                new ConfigProperty("spring.datasource.driverClassName", "com.mysql.cj.jdbc.Driver"),
//                new ConfigProperty("spring.datasource.username", "root"),
//                new ConfigProperty("spring.datasource.password", "password"),
//                new ConfigProperty("spring.jpa.database-platform", "org.hibernate.dialect.MySQL8Dialect"),
//                new ConfigProperty("spring.jpa.defer-datasource-initialization", true),
//                new ConfigProperty("spring.jpa.hibernate.ddl-auto", "create"),
//                new ConfigProperty("spring.jpa.show-sql", false),
//                // bonus:
//                new ConfigProperty("bonus.signup.enable", true),
//                new ConfigProperty("bonus.signup.calculation-type", "FLAT", "FLAT, PERCENTAGE"),
//                new ConfigProperty("bonus.signup.flat-amount", 100),
//                new ConfigProperty("bonus.referral.enable", true),
//                new ConfigProperty("bonus.referral.calculation-type", "FLAT", "FLAT, PERCENTAGE"),
//                new ConfigProperty("bonus.referral.percentage-rate", 0.5, "FLAT, PERCENTAGE"),
//                new ConfigProperty("bonus.referral.flat-amount", 300),
//                // Email Config
//                new ConfigProperty("mail.host", "smtp.gmail.com"),
//                new ConfigProperty("mail.port", 587),
//                new ConfigProperty("mail.username", "cloud.arpan@gmail.com"),
//                new ConfigProperty("mail.password", "yrcu swoe gbpm lpwo"),
//                new ConfigProperty("mail.smtp.auth", true),
//                new ConfigProperty("mail.starttls.enable", true)
//        );
//        saveAll(configs);
//    }
//
//    private void saveAll(List<ConfigProperty> props) {
//        List<ConfigProperty> configs = props.stream().map(prop -> {
//            prop.setProfile("default");
//            prop.setApplication("nft_app");
//            return prop;
//        }).collect(Collectors.toList());
//        repository.saveAll(configs);
//
//    }
//}
