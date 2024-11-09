package com.example.int221backend.repositories.shared;

import com.example.int221backend.entities.shared.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    User findByUsername(String userName);
    Optional<User> findByEmail(String email);
}
