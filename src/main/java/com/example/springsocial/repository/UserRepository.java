package com.example.springsocial.repository;

import com.example.springsocial.entity.DbUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<DbUser, Long> {
    boolean existsByUsername(String username);

    Optional<DbUser> findByUsername(String username);

    List<DbUser> findByEnabled(boolean enabled);
}
