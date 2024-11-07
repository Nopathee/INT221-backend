package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabRepository extends JpaRepository<Collaborators,Integer> {
    List<Collaborators> findByBoard(Board board);

    Collaborators findByUser_EmailAndBoard_BoardId(String email, String boardId);
    Collaborators findByUser_OidAndBoard_BoardId(String oid , String boardId);
    Optional<Collaborators> findByBoardAndUser(Board board, UserLocal oid);
    List<Collaborators> findByUser_Oid(String oid);

    List<Collaborators> findByUser(UserLocal user);


}
