package com.example.int221backend.repositories.local;


import com.example.int221backend.entities.local.TaskV3;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskV3Repository extends JpaRepository<TaskV3, Integer> {
    List<TaskV3> findByStatusId(Integer statusId);
}

