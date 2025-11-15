package com.piras.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.piras.backend.model.User;

public interface Repository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
