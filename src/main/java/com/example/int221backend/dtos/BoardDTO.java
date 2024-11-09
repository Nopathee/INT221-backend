package com.example.int221backend.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@JsonPropertyOrder({"id", "boardName", "visibility", "owner"})
public class BoardDTO {
    @Size(max = 10)
    private String id;
    @Size(max = 120)
    private String boardName;
    private String visibility;
    private UserLocalResponse owner;
    private String accessRight;
}
