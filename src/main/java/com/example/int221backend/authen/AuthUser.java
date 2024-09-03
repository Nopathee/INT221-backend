package com.example.int221backend.authen;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class AuthUser extends User implements Serializable {

    // Constructor ที่ไม่มี authorities
    public AuthUser() {
        super("anonymous", "", new ArrayList<GrantedAuthority>());
    }

    // Constructor ที่มี authorities
    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    // Constructor ที่ไม่มี authorities และใช้ default authorities
    public AuthUser(String username, String password) {
        this(username, password, new ArrayList<GrantedAuthority>());
    }
}
