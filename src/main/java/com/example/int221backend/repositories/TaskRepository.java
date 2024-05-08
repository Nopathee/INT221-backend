package com.example.int221backend.repositories;

import com.example.int221backend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByStatusId(String statusId);
}
