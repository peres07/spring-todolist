package com.learningjava.todolist.repository;

import com.learningjava.todolist.entity.TaskModal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModal, UUID> {
    List<TaskModal> findByUserId(UUID userId);
}
