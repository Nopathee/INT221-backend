package com.example.int221backend.dtos;

import com.example.int221backend.entities.local.Status;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "description", "assignees", "status"})
public class TaskAndStatusShort {
    private String id;
    @Size(max = 100, message = "title size must be between 0 and 100.")
    private String title;
    @Size(max = 500, message = "description size must be between 0 and 500.")
    private String description;
    @Size(max = 30, message = "assignees size must be between 0 and 30.")
    private String assignees;
    private Status status;

    public void trimValues() {
        if (this.title != null) {
            this.title = this.title.trim();
        }
        if (this.description != null) {
            this.description = this.description.trim();
        }
        if (this.assignees != null) {
            this.assignees = this.assignees.trim();
        }
    }
}
