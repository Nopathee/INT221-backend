package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name,Integer id);
}
