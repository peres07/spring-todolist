package com.learningjava.todolist.controllers;

import com.learningjava.todolist.response.ResponseModal;
import com.learningjava.todolist.repository.ITaskRepository;
import com.learningjava.todolist.entity.TaskModal;
import com.learningjava.todolist.utils.Utils;
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
    public ResponseEntity<Object> create(@RequestBody TaskModal taskModal, HttpServletRequest request, ResponseModal responseModal) {

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModal.getStartAt()) || currentDate.isAfter(taskModal.getEndAt())) {
            responseModal.setMessage("A data de início / término deve ser maior que a data atual.");
            responseModal.setStatusCode(400);
            return ResponseEntity.status(400).body(responseModal);
        }

        if (taskModal.getStartAt().isAfter(taskModal.getEndAt())) {
            responseModal.setMessage("A data de início deve ser menor que a data de término.");
            responseModal.setStatusCode(400);
            return ResponseEntity.status(400).body(responseModal);

        }

        taskModal.setUserId((UUID) request.getAttribute("userId"));

        responseModal.setData(this.taskRepository.save(taskModal));
        responseModal.setMessage("Tarefa criada com sucesso.");
        responseModal.setStatusCode(200);
        return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);
    }

    @GetMapping("/")
    public ResponseEntity<ResponseModal> listAll(HttpServletRequest request, ResponseModal responseModal) {

        List<TaskModal> tasks = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));

        responseModal.setData(tasks);
        responseModal.setMessage("Tarefas listadas com sucesso.");
        responseModal.setStatusCode(200);
        return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);
    }

    private TaskModal findTaskByIdAndUserId(UUID id, HttpServletRequest request) {
        var task = this.taskRepository.findById(id).orElse(null);

        if (task != null && !task.getUserId().equals(request.getAttribute("userId"))) {
            return null;
        }

        return task;
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> update(@RequestBody TaskModal taskModal, HttpServletRequest request, @PathVariable UUID id, ResponseModal responseModal) {

        var task = findTaskByIdAndUserId(id, request);

        if (task == null) {
            responseModal.setMessage("Tarefa não encontrada.");
            responseModal.setStatusCode(404);
            return ResponseEntity.status(404).body(responseModal);
        }

        Utils.copyNonNullProperties(taskModal, task);

        responseModal.setData(this.taskRepository.save(task));
        responseModal.setMessage("Tarefa editada com sucesso.");
        responseModal.setStatusCode(200);
        return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<Object> show(HttpServletRequest request, @PathVariable UUID id, ResponseModal responseModal) {

        var task = findTaskByIdAndUserId(id, request);

        if (task == null) {
            responseModal.setMessage("Tarefa não encontrada.");
            responseModal.setStatusCode(404);
            return ResponseEntity.status(404).body(responseModal);
        }

        responseModal.setData(task);
        responseModal.setMessage("Consulta realizada com sucesso.");
        responseModal.setStatusCode(200);
        return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModal> delete(HttpServletRequest request, @PathVariable UUID id, ResponseModal responseModal) {

        var task = findTaskByIdAndUserId(id, request);

        if (task == null) {
            responseModal.setMessage("Tarefa não encontrada.");
            responseModal.setStatusCode(404);
            return ResponseEntity.status(404).body(responseModal);
        }

        this.taskRepository.delete(task);

        responseModal.setMessage("Tarefa deletada com sucesso.");
        responseModal.setStatusCode(200);

        return ResponseEntity.status(200).body(responseModal);
    }
}
