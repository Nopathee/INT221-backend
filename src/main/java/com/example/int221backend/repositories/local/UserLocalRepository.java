package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.UserLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLocalRepository extends JpaRepository<UserLocal,String> {
    Optional<UserLocal> findByOid(String oid);

    Optional<UserLocal> findByEmail(String email);
}
