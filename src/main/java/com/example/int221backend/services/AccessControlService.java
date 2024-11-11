package com.example.int221backend.services;

import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.exception.AccessDeniedException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.CollabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CollabRepository collabRepository;

    @Autowired
    private AccessRightService accessRightService;

    public boolean hasAccess(String userId, String boardId, String token, AccessRight requiredAccess) {
        System.out.println(boardId);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found!!"));

        boolean isOwner = board.getOwner().getOid().equals(userId);
        boolean isPrivate = board.getVisibility() == BoardVisi.PRIVATE;
        boolean isPublic = board.getVisibility() == BoardVisi.PUBLIC;
        boolean hasValidToken = (token != null && !token.isEmpty());

        // ตรวจสอบการเข้าถึงตามสถานะของบอร์ด
        if (isPublic && !hasValidToken) {
            return true; // อนุญาตเข้าถึงบอร์ด public โดยไม่มี token
        }

        if (isPrivate  && !hasValidToken) {
            throw new AccessDeniedException("Access denied: private board requires authentication.");
        }

        if (isOwner || (isPublic && requiredAccess == AccessRight.READ)) {
            return true; // อนุญาตให้เจ้าของหรือบอร์ด public เข้าถึง
        }

        // ตรวจสอบสิทธิ์ของผู้ร่วมงาน
        return accessRightService.hasCollaboratorAccess(userId, boardId, requiredAccess);
    }
}
