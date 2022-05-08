package com.appjwtrealemailingauditing.repository;

import com.appjwtrealemailingauditing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndEmailCode( String sendingEmail, String emailCode);

    Optional<User> findByEmail(String email);
}
