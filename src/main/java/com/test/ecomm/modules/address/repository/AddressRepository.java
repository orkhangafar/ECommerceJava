package com.test.ecomm.modules.address.repository;

import com.test.ecomm.modules.address.entity.Address;
import com.test.ecomm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    boolean existsByUserAndIsDefaultTrue(User user);
}
