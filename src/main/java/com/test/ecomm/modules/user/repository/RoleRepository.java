package com.test.ecomm.modules.user.repository;

import com.test.ecomm.modules.user.entity.AppRole;
import com.test.ecomm.modules.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole roleName);
}
