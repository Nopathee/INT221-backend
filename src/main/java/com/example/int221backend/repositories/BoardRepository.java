package com.example.int221backend.repositories;

import com.example.int221backend.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,String> {
    Board findByOwner_Oid(String oid);
}
