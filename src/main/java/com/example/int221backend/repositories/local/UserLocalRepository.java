package com.example.int221backend.repositories.local;

import com.example.int221backend.entities.local.UserLocal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocalRepository extends JpaRepository<UserLocal,String> {
    UserLocal findByOid(String oid);
}
