package com.learningjava.todolist.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModal, UUID> {
    List<TaskModal> findByUserId(UUID userId);
}
