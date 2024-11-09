package com.example.int221backend.repositories.local;


import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.SharedBoard;
import com.example.int221backend.entities.local.UserLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedBoardRepository extends JpaRepository<SharedBoard, String> {
    SharedBoard findByBoard(Board board);
    SharedBoard findByBoardAndOwner(Board board, UserLocal owner);

}
