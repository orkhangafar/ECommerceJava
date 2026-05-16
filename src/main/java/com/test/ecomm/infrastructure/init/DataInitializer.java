package com.test.ecomm.infrastructure.init;

import com.test.ecomm.modules.user.entity.AppRole;
import com.test.ecomm.modules.user.entity.Role;
import com.test.ecomm.modules.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (AppRole roleName : AppRole.values()) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                roleRepository.save(new Role(null, roleName));
            }
        }
    }
}
