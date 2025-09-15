package com.trustai.aggregator;

import com.trustai.common.auth.repository.RoleRepository;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.domain.user.Role;
import com.trustai.common.domain.user.User;
import com.trustai.common.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    String adminPassword = "$2a$12$7Q.ejHVLMMtPyBu4VQEULO8TnoJlX1xSuzjLn07GjLmeNwuJrt8Yy"; // admin@123
    String testPassword1 = "$2a$12$4sLp.sNltnRtf7tsDD4m1Oz1LxRY2MmNTZqe8bWnmt7/3lRW68ILq"; // test1
    String testPassword2 = "$2a$12$yXQ9cmGqwXJav5utBBHg3uG.fxV0.fRISbg6mNx5lH.nh1PEuAFPi"; // test2

    @Override
    public void run(String... args) throws Exception {
        createUserIfNotExists("root", "root@trustai.com", adminPassword, "REF1", CommonConstants.ROLE_ADMIN, new BigDecimal("50000"));
        createUserIfNotExists("test1", "test1@test.com", testPassword1, "REF2", CommonConstants.ROLE_USER, new BigDecimal("50000"));
    }

    private void createUserIfNotExists(String username, String email, String password, String referralCode, String roleName, BigDecimal balance) {
        boolean exists = userRepository.existsByEmail(email);
        if (exists) return;

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Default role not found: " + roleName));

        User user = new User(username, "RANK_1", balance);
        user.setEmail(email);
        user.setRoles(Set.of(role));
        user.setPassword(password);
        user.setReferralCode(referralCode);

        userRepository.save(user);
    }

}
