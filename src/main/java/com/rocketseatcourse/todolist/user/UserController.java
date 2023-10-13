package com.rocketseatcourse.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
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

    @PostMapping({"/", ""})
    public ResponseEntity<?> create(@RequestBody UserModal userModal) {
        var user = this.userRepository.findByUsername(userModal.getUsername());

        if(user != null) {
            return ResponseEntity.status(400).body("Usuário já existe.");
        }

        userModal.setPassword(BCrypt.withDefaults().hashToString(12, userModal.getPassword().toCharArray()));

        return ResponseEntity.status(201).body(this.userRepository.save(userModal));

    }
}
