package com.rocketseatcourse.todolist.task;

import com.rocketseatcourse.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody TaskModal taskModal, HttpServletRequest request) {

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModal.getStartAt()) || currentDate.isAfter(taskModal.getEndAt())) {
            return ResponseEntity.status(400).body("A data de início / término deve ser maior que a data atual.");
        }

        if (taskModal.getStartAt().isAfter(taskModal.getEndAt())) {
            return ResponseEntity.status(400).body("A data de início deve ser menor que a data de término.");
        }

        taskModal.setUserId((UUID) request.getAttribute("userId"));

        return ResponseEntity.status(200).body(this.taskRepository.save(taskModal));
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModal>> listAll(HttpServletRequest request) {

        List<TaskModal> tasks = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));

        return ResponseEntity.status(200).body(tasks);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> update(@RequestBody TaskModal taskModal, HttpServletRequest request, @PathVariable UUID id) {

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(404).body("Tarefa não encontrada.");
        }

        if (!task.getUserId().equals(request.getAttribute("userId"))) {
            return ResponseEntity.status(401).body("Usuário não autorizado.");
        }

        Utils.copyNonNullProperties(taskModal, task);

        return ResponseEntity.status(200).body(this.taskRepository.save(task));
    }
}
