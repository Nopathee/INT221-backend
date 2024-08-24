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
    public AuthUser() {
        super("anonymous", "", new ArrayList<GrantedAuthority>());
    }

    public AuthUser(String userName, String password) {
        super(userName, password, new ArrayList<GrantedAuthority>());
    }

    public AuthUser(String userName, String password, Collection<? extends
                GrantedAuthority> authorities) {
        super(userName, password, authorities);
    }
}
