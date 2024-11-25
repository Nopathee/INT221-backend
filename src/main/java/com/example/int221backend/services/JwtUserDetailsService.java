package com.example.int221backend.services;

import com.example.int221backend.authen.AuthUser;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.entities.shared.User;
import com.example.int221backend.exception.CustomUsernameNotFoundException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.UserLocalRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLocalRepository userListRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws CustomUsernameNotFoundException {
        User user = userRepository.findByUsername(userName);

        if (user == null) {
            throw new CustomUsernameNotFoundException("The Username or Password is incorrect !!!");
        }

        UserLocal userLocal = new UserLocal();
        userLocal.setOid(user.getOid());
        userLocal.setUsername(user.getUsername());
        userLocal.setName(user.getName());
        userLocal.setEmail(user.getEmail());
        userLocal.setCreatedOn(ZonedDateTime.now());
        userLocal.setUpdatedOn(ZonedDateTime.now());
        userListRepository.save(userLocal);

        return new AuthUser(user.getUsername(), user.getPassword());
    }
    public User getUserByOid(String oid) {
        Optional<User> userOptional = userRepository.findByOid(oid);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UsernameNotFoundException("User not found with oid: " + oid);
        }
    }

    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ItemNotFoundException("user not found"));

        UserLocal userLocal = new UserLocal();
        userLocal.setOid(user.getOid());
        userLocal.setUsername(user.getUsername());
        userLocal.setName(user.getName());
        userLocal.setEmail(user.getEmail());
        userLocal.setCreatedOn(ZonedDateTime.now());
        userLocal.setUpdatedOn(ZonedDateTime.now());
        userListRepository.save(userLocal);

        return new AuthUser(user.getUsername(), user.getPassword());
    }
}
