package com.test.ecomm.modules.auth.repository;

import com.test.ecomm.modules.auth.entity.TokenType;
import com.test.ecomm.modules.auth.entity.VerificationToken;
import com.test.ecomm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByUserAndType(User user, TokenType type);

    void deleteByUserAndType(User user, TokenType type);

    Optional<VerificationToken> findByCodeAndType(String code, TokenType type);
}