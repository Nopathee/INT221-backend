package com.example.int221backend.user_entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,String> {
    Board findByOwner_Oid(String oid);
}
