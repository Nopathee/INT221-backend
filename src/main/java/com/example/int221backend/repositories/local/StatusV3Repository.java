package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusV3Repository extends JpaRepository<Status, Integer> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name,Integer id);

    List<Status> findByBoard(Board board);
}
