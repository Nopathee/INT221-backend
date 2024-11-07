package com.example.int221backend.dtos;

import com.example.int221backend.entities.AccessRight;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
public class ShowCollabDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight access_right;
    private Date added_on;
}
