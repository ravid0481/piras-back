package com.piras.backend.controller;

import com.piras.backend.model.Incident;
import com.piras.backend.repo.IncidentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = {"*"})
public class IncidentController {

    private final IncidentRepository repo;

    public IncidentController(IncidentRepository repo) {
        this.repo = repo;
    }

    // ✅ Existing JSON-based endpoint
    @PostMapping
    public Incident createIncident(@RequestBody Incident incident) {
        if (incident.getCreatedAt() == null) {
            incident.setCreatedAt(Instant.now());
        }
        return repo.save(incident);
    }

    // ✅ Add this new endpoint for file uploads
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Incident createWithMedia(
            @RequestParam(required = false) String email,
            @RequestParam String name,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String location,
            @RequestPart(required = false) MultipartFile file
    ) throws IOException {

        Incident in = new Incident();
        in.setEmail(email);
        in.setName(name);
        in.setTitle(title);
        in.setCategory(category);
        in.setDescription(description);
        in.setLocation(location);
        in.setCreatedAt(Instant.now());

        // 🟩 Save file if provided
        if (file != null && !file.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            in.setMediaUrl("/files/" + fileName);
            in.setMediaType(file.getContentType());
        }

        return repo.save(in);
    }

    // ✅ Get user-specific incidents
    @GetMapping
    public List<Incident> getByEmail(@RequestParam String email) {
        return repo.findByEmailOrderByCreatedAtDesc(email);
    }

    // ✅ Get all incidents
    @GetMapping("/all")
    public List<Incident> getAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }
}
