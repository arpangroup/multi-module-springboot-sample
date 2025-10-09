package com.trustai.aggregator;

import com.trustai.common.auth.repository.RoleRepository;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExists(CommonConstants.ROLE_ADMIN);
        createRoleIfNotExists(CommonConstants.ROLE_USER);
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            return; // skip if role already exists
        }
        Role role = new Role(roleName);
        roleRepository.save(role);
    }
}
