package com.example.int221backend.services;

import com.example.int221backend.repositories.local.UserLocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLocalService {
    @Autowired
    private UserLocalRepository userLocalRepository;

}
