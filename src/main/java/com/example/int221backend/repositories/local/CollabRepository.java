package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Collaborators;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollabRepository extends JpaRepository<Collaborators,Integer> {
    List<Collaborators> findByBoard_BoardId(String boardId);

    Collaborators findByUser_EmailAndBoard_BoardId(String email, String boardId);
    Collaborators findByUser_OidAndBoard_BoardId(String oid , String boardId);
}
