package com.piras.backend.controller;

import com.piras.backend.model.User;
import com.piras.backend.repo.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class AuthController {

    @Autowired
    private Repository repository;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        String email = safe(user.getEmail()).toLowerCase();
        String pass  = safe(user.getPassword());
        if (repository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        user.setEmail(email);
        user.setPassword(pass); // TEMP: plain text
        repository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User req) {
        String inEmail = safe(req.getEmail()).toLowerCase();
        String inPass  = safe(req.getPassword());
        var existing = repository.findByEmail(inEmail);
        if (existing == null) return ResponseEntity.status(401).body("Invalid email or password");
        if (!inPass.equals(safe(existing.getPassword())))
            return ResponseEntity.status(401).body("Invalid email or password");
        return ResponseEntity.ok("Login success");
    }

    @GetMapping("/ping") public String ping(){ return "pong"; }
    private static String safe(String s){ return s==null? "" : s.trim(); }
}
