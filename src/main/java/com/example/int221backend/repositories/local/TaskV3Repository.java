package com.example.int221backend.repositories.local;


import com.example.int221backend.entities.local.TaskV3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskV3Repository extends JpaRepository<TaskV3, String> {

    @Transactional("projectManagementTransactionManager")
    List<TaskV3> findByStatusId(Integer statusId);

    List<TaskV3> findByBoard_BoardId(String boardId);

    @Query("SELECT t.status.id, COUNT(t) FROM TaskV3 t WHERE t.board.boardId = :boardId GROUP BY t.status.id")
    List<Object[]> countTasksByStatusForBoard(@Param("boardId") String boardId);

}

