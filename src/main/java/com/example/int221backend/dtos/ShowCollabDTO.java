package com.example.int221backend.dtos;

import com.example.int221backend.entities.AccessRight;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ShowCollabDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight access_right;
    private ZonedDateTime added_on;
}
