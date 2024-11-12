package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusV3Repository extends JpaRepository<Status, Integer> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name,Integer id);

    List<Status> findByBoard(Board board);

    Status findByBoardAndId(Board board, Integer statusId);

    Status findByIdAndBoard_BoardId(Integer id, String boardId);

    @Query("SELECT s FROM Status s WHERE s.board.boardId = :boardId")
    List<Status> findAllStatus(String boardId);

    @Query("SELECT s FROM Status s WHERE s.id = :statusId AND s.board.boardId = :boardId")
    Status findStatusByIdAndBoard(Integer statusId, String boardId);

    @Query("SELECT s FROM Status s WHERE s.board.boardId = :boardId AND s.name = :statusName")
    Status findDefaultStatus(String boardId,String statusName);

}
