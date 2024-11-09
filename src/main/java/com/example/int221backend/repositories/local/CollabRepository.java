package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollabRepository extends JpaRepository<Collaborators,Integer> {
    List<Collaborators> findByBoard(Board board);

    Collaborators findByUser_EmailAndBoard_BoardId(String email, String boardId);
    Collaborators findByUser_OidAndBoard_BoardId(String oid , String boardId);
    Optional<Collaborators> findByBoardAndUser(Board board, UserLocal oid);
    List<Collaborators> findByUser_Oid(String oid);

    List<Collaborators> findByUser(UserLocal user);

    List<Collaborators> findAllByUser_Oid(String oid);

    List<Collaborators> findByBoard_BoardId(String bId);

    @Query("SELECT c FROM Collaborators c WHERE c.board.boardId = :boardId AND c.user.email = :email")
    Collaborators findByBoardIdAndUserEmail(@Param("boardId") String boardId, @Param("email") String email);
}
