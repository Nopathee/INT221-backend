package com.example.int221backend.repositories;

import com.example.int221backend.entities.TaskV2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskV2Repository extends JpaRepository<TaskV2, Integer> {
    List<TaskV2> findByStatusId(Integer statusId);
}
