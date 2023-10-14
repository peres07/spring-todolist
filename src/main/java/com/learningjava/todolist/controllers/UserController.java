package com.learningjava.todolist.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.learningjava.todolist.infra.security.TokenService;
import com.learningjava.todolist.response.ResponseModal;
import com.learningjava.todolist.repository.IUserRepository;
import com.learningjava.todolist.entity.UserModal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping({"/create"})
    public ResponseEntity<ResponseModal> create(@RequestBody UserModal userModal, ResponseModal responseModal) {
        var user = this.userRepository.findByUsername(userModal.getUsername());

        if(user != null) {
            responseModal.setMessage("Usuário já existe.");
            responseModal.setStatusCode(409);
            return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(userModal.getPassword());
        userModal.setPassword(encryptedPassword);

        responseModal.setData((Object) this.userRepository.save(userModal));
        responseModal.setMessage("Usuário criado com sucesso.");
        responseModal.setStatusCode(201);
        return ResponseEntity.status(responseModal.getStatusCode()).body(responseModal);

    }

    @PostMapping({"/login"})
    public ResponseEntity<ResponseModal> login(ResponseModal responseModal, @RequestHeader("Authorization") String requestHeader) {

        var authEncoded = requestHeader.substring("Basic".length()).trim();
        var authDecoded = new String(java.util.Base64.getDecoder().decode(authEncoded));

        String[] credentials = authDecoded.split(":");
        String username = credentials[0];
        String password = credentials[1];

        var usernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserModal) auth.getPrincipal());

        responseModal.setData(token);

        responseModal.setMessage("Usuário autenticado com sucesso.");
        responseModal.setStatusCode(200);

        return ResponseEntity.status(200).body(responseModal);
    }

}
