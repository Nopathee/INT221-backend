package com.example.int221backend.controllers;

import com.example.int221backend.dtos.*;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.exception.*;
import com.example.int221backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th","https://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v3/boards/{boardId}/collabs")
public class CollabController {

    @Autowired
    private CollabService collabService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private AccessRightService accessRightService;

    @GetMapping("")
    public ResponseEntity<Object> getAllCollaborators(
            @PathVariable String boardId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        BoardDTO board = boardService.getBoardByBoardId(boardId);

        String accessToken = (token != null && !token.isEmpty()) ? token.substring(7) : null;
        String userId = (accessToken != null) ? jwtService.getOidFromToken(accessToken) : null;

        boolean checkAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.READ);
        if (!checkAccess) {
            throw new AccessDeniedException("Access denied");
        }

        ListShowCollabDTO response = collabService.getAllCollab(boardId);
        return ResponseEntity.ok(response);
    }

    // Get a specific collaborator by their OID for a specific board
    @GetMapping("/{collabOid}")
    public ResponseEntity<Object> getCollaboratorByOid(
            @PathVariable String boardId,
            @PathVariable String collabOid,
            @RequestHeader(value = "Authorization", required = false) String token) {

        BoardDTO boardDTO = boardService.getBoardByBoardId(boardId);

        String accessToken = (token != null && !token.isEmpty()) ? token.substring(7) : null;
        String userId = (accessToken != null) ? jwtService.getOidFromToken(accessToken) : null;

        boolean checkAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.READ);
        if (!checkAccess) {
            throw new AccessDeniedException("Access denied");
        }

        ShowCollabDTO response = collabService.getCollabByOid(boardId, collabOid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<Object> addCollaborator(
            @PathVariable String boardId,
            @RequestBody(required = false) AddCollabDTO req,
            @RequestHeader(value = "Authorization", required = false) String token) {

        String accessToken = (token != null && !token.isEmpty()) ? token.substring(7) : null;
        String userId = (accessToken != null) ? jwtService.getOidFromToken(accessToken) : null;

        BoardDTO board = boardService.getBoardByBoardId(boardId);

        String collaboratorEmail = req.getEmail();
        if (collaboratorEmail == null || collaboratorEmail.isEmpty()) {
            throw new AccessDeniedException("Email cannot be empty.");
        }

        String accessRight = req.getAccessRight();
        if (accessRight == null || accessRight.isEmpty()) {
            throw new BadRequestException("access right cannot be empty");
        }

        if (board.getOwner().getEmail().equalsIgnoreCase(req.getEmail())) {
            throw new ConflictException("Owner cannot be added as a collaborator.");
        }

        // ตรวจสอบ accessRight
        AccessRight accessRightEnum = accessRightService.validateAccessRight(req.getAccessRight());

        // ตรวจสอบการเข้าถึง
        boolean checkAccess = accessControlService.hasAccess(userId, boardId, token, accessRightEnum);
        if (!checkAccess) {
            throw new AccessDeniedException("Access denied");
        }

        ShowCollabDTO collaboratorResponse = collabService.addCollaborator(boardId, collaboratorEmail, accessRight);

        BoardWithCollabDTO responseDTO = new BoardWithCollabDTO();
        responseDTO.setBoardId(board.getId());
        responseDTO.setBoardName(board.getBoardName());

        ListShowCollabDTO collaborators = collabService.getAllCollab(boardId);
        responseDTO.setCollaborators(collaborators.getShowCollabDTOS());

        return ResponseEntity.status(201).body(responseDTO);

    }

}
