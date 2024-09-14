package com.example.int221backend.entities;

import com.example.int221backend.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name,Integer id);
}
