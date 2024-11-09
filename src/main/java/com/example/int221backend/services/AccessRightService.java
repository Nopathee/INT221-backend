package com.example.int221backend.services;

import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.repositories.local.CollabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRightService {

    @Autowired
    private CollabRepository collabRepository;

    public AccessRight validateAccessRight(String accessRight) {
        if (accessRight == null || accessRight.isEmpty()) {
            throw new BadRequestException("Access right cannot be empty.");
        }

        try {
            return AccessRight.valueOf(accessRight.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid access right. Allowed values are READ or WRITE.");
        }
    }

    public boolean hasCollaboratorAccess(String userId, String boardId, AccessRight requiredAccess) {
        Collaborators collaborator = collabRepository.findByUser_OidAndBoard_BoardId(userId, boardId);
        if (collaborator != null) {
            if (requiredAccess == AccessRight.READ) {
                return collaborator.getAccessRight() == AccessRight.READ ||
                        collaborator.getAccessRight() == AccessRight.WRITE; // READ or WRITE
            }
            if (requiredAccess == AccessRight.WRITE) {
                return collaborator.getAccessRight() == AccessRight.WRITE; // Only WRITE
            }
        }
        return false; // No access
    }
}
