package com.learningjava.todolist.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.learningjava.todolist.response.ResponseModal;
import com.learningjava.todolist.repository.IUserRepository;
import com.learningjava.todolist.entity.UserModal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping({"/create"})
    public ResponseEntity<?> create(@RequestBody UserModal userModal, ResponseModal responseModal) {
        var user = this.userRepository.findByUsername(userModal.getUsername());

        if(user != null) {
            responseModal.setMessage("Usuário já existe.");
            responseModal.setStatusCode(409);
            return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);
        }

        userModal.setPassword(BCrypt.withDefaults().hashToString(12, userModal.getPassword().toCharArray()));

        responseModal.setData((Object) this.userRepository.save(userModal));
        responseModal.setMessage("Usuário criado com sucesso.");
        responseModal.setStatusCode(201);
        return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);

    }

    @PostMapping({"/login"})
    public ResponseEntity login(HttpServletRequest request, ResponseModal responseModal) {
        return null;
    }

}
