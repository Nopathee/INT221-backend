package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddCollaboratorDTO;
import com.example.int221backend.dtos.ShowCollabDTO;
import com.example.int221backend.dtos.SimpleTaskV3DTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.TaskV3;
import com.example.int221backend.exception.ForBiddenException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.CollabService;
import com.example.int221backend.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("")
    public ResponseEntity<Object> getAllCollaborators(
            @PathVariable String boardId,
            @RequestHeader(value = "Authorization", required = false) String token) {

        // Check if the board exists
        if (!boardService.existsById(boardId)) {
            throw new ItemNotFoundException("board not found");
        }

        // Fetch the board to check its visibility
        Board board = boardService.getBoardByBoardId(boardId);
        boolean isPublic = board.getVisibility().toString().equalsIgnoreCase("public");

        // If no token is provided, handle public access
        if (token == null || token.isEmpty()) {
            if (isPublic) {
                List<ShowCollabDTO> collaborators = collabService.getAllCollab(boardId);
                return ResponseEntity.ok(collaborators);
            } else {
                throw new ForBiddenException("Access denied");
            }
        }

        // Extract OID from token
        String afterSubToken = token.substring(7);
        String oid = jwtService.getOidFromToken(afterSubToken);
        boolean isOwner = board.getOwner().getOid().equals(oid);
        boolean isCollaborator = collabService.isCollaborator(oid, boardId); // Check if user is a collaborator

        // Check if the user is the owner or a collaborator or if the board is public
        if (isOwner || isCollaborator || isPublic) {
            List<ShowCollabDTO> collaborators = collabService.getAllCollab(boardId);
            return ResponseEntity.ok(collaborators);
        } else {
            throw new ForBiddenException("Access denied");
        }
    }

    // Get a specific collaborator by their OID for a specific board
    @GetMapping("/{collabOid}")
    public ResponseEntity<Object> getCollaboratorByOid(
            @PathVariable String boardId,
            @PathVariable String collabOid,
            @RequestHeader(value = "Authorization", required = false) String token) {

        // Check if the board exists
        if (!boardService.existsById(boardId)) {
            throw new ItemNotFoundException("board not found");
        }

        // Fetch the board to check its visibility
        Board board = boardService.getBoardByBoardId(boardId);
        boolean isPublic = board.getVisibility().toString().equalsIgnoreCase("public");

        // If no token is provided, handle public access
        if (token == null || token.isEmpty()) {
            if (isPublic) {
                ShowCollabDTO collaborator = collabService.getCollabByOid(boardId, collabOid);
                return ResponseEntity.ok(collaborator);
            } else {
                throw new ForBiddenException("Access denied");
            }
        }

        // Extract OID from token
        String afterSubToken = token.substring(7);
        String oid = jwtService.getOidFromToken(afterSubToken);
        boolean isOwner = board.getOwner().getOid().equals(oid);
        boolean isCollaborator = collabService.isCollaborator(oid, boardId); // Check if user is a collaborator

        // Check if the user is the owner or a collaborator or if the board is public
        if (isOwner || isCollaborator || isPublic) {
            ShowCollabDTO collaborator = collabService.getCollabByOid(boardId, collabOid);
            return ResponseEntity.ok(collaborator);
        } else {
            throw new ForBiddenException("Access denied");
        }
    }

    @PostMapping("")
    public ResponseEntity<Object> addCollaborator(
            @PathVariable String boardId,
            @RequestBody AddCollaboratorDTO addCollaboratorDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {

        // Check if the board exists
        if (!boardService.existsById(boardId)) {
            throw new ItemNotFoundException("board not found");
        }

        // Extract OID from token
        String afterSubToken = token.substring(7);
        String oid = jwtService.getOidFromToken(afterSubToken);

        // Check if the user is authorized to add collaborators (owner or collaborator)
        Board board = boardService.getBoardByBoardId(boardId);
        boolean isOwner = board.getOwner().getOid().equals(oid);
        boolean isCollaborator = collabService.isCollaborator(oid, boardId);

        if (!isOwner && !isCollaborator) {
            throw new ForBiddenException("Access denied");
        }

        // Add the collaborator
        ShowCollabDTO newCollaborator = collabService.addCollaborator(boardId, addCollaboratorDTO.getEmail(), addCollaboratorDTO.getAccessRight());
        return ResponseEntity.status(HttpStatus.CREATED).body(newCollaborator);
    }

}
