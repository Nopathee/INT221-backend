package com.example.int221backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private String id;  // This is now the uid

    @NotBlank(message = "Board name cannot be empty")
    @Size(max = 120, message = "Board name cannot exceed 120 characters")
    private String name;

    private String visibility;
    private PMUserDTO owner;
    private List<CollaboratorDTO> collaborators;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PMUserDTO {
        private String oid;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollaboratorDTO {
        private String oid;
        private String name;
        private String access_right;
    }
}
