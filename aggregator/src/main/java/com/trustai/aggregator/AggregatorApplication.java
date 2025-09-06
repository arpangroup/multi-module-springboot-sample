package com.trustai.aggregator;

import com.trustai.common.auth.repository.RoleRepository;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.domain.user.Role;
import com.trustai.common.domain.user.User;
import com.trustai.common.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.trustai")
@ComponentScan(basePackages = {"com.trustai"})
@EnableMethodSecurity
@EnableAsync
public class AggregatorApplication implements CommandLineRunner {
    @Autowired UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    String adminPassword = "$2a$12$7Q.ejHVLMMtPyBu4VQEULO8TnoJlX1xSuzjLn07GjLmeNwuJrt8Yy"; // admin@123
    String testPassword1 = "$2a$12$4sLp.sNltnRtf7tsDD4m1Oz1LxRY2MmNTZqe8bWnmt7/3lRW68ILq"; // test1
    String testPassword2 = "$2a$12$yXQ9cmGqwXJav5utBBHg3uG.fxV0.fRISbg6mNx5lH.nh1PEuAFPi"; // test2

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initUsers();
        //initNotifications();
    }

    private void initRoles() {
        Role roleAdmin = new Role(CommonConstants.ROLE_ADMIN);
        roleRepository.save(roleAdmin);

        Role roleUser = new Role(CommonConstants.ROLE_USER);
        roleRepository.save(roleUser);
    }

    private void initUsers() {
        User user = userRepository.findById(1L).orElse(null);
        Role roleAdmin = roleRepository.findByName(CommonConstants.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Default role not found: " + CommonConstants.ROLE_ADMIN));
        Role roleUser = roleRepository.findByName(CommonConstants.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found: " + CommonConstants.ROLE_USER));

        if (user != null) return;
		/*User rootUser = new User("U1", "RANK_1", BigDecimal.ZERO);
		rootUser.setEmail("root@trustai.com");
		rootUser.addRole("USER");
		userRepository.save(rootUser);*/

        User user_admin = new User("root", "RANK_1", new BigDecimal("50000"));
        user_admin.setEmail("root@trustai.com");
        user_admin.setRoles(new HashSet<>(List.of(roleAdmin)));
        user_admin.setPassword(adminPassword);
        user_admin.setReferralCode("REF1");
        userRepository.save(user_admin);

        User user1 = new User("test1", "RANK_1", new BigDecimal("50000"));
        user1.setEmail("test1@test.com");
        user1.setRoles(new HashSet<>(List.of(roleUser)));
        user1.setPassword(testPassword1);
        user1.setReferralCode("REF2");
        userRepository.save(user1);

		/*User user2 = new User("test2", "RANK_1", new BigDecimal("50000"));
		user2.setEmail("test2@gmail.com");
		user2.addRole("USER");
		user_admin.setPassword(testPassword2);
		userRepository.save(user2);*/
    }
}
