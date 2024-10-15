package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,String> {
     List<Board> findByOwner_Oid(String oid);

     Board getBoardByBoardId(String boardId);



}
