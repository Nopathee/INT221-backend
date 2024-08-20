package com.example.int221backend.user_entities;

import com.example.int221backend.entities.StatusRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String userName);
}
