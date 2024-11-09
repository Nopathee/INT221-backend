package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.entities.shared.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,String> {
//     List<Board> findByOwner_Oid(String oid);

     Board getBoardByBoardId(String boardId);

     List<Board> findByVisibility(BoardVisi visibility);

     List<Board> findByOwner(UserLocal user);

     List<Board> findAllByOwner_Oid(String oid);


}
