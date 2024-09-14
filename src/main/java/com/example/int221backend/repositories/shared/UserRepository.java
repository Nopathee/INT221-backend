package com.example.int221backend.repositories.shared;

import com.example.int221backend.entities.shared.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String userName);
}
