package com.example.int221backend.services;

import com.example.int221backend.dtos.ShowCollabDTO;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.BoardCollabId;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.entities.shared.User;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.CollabRepository;
import com.example.int221backend.repositories.local.UserLocalRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollabService {

    @Autowired
    private CollabRepository collabRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserLocalRepository userLocalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ShowCollabDTO> getAllCollab(String boardId) {
        if (!boardRepository.existsById(boardId)){
            throw new ItemNotFoundException("board not found");
        }

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found"));

        List<Collaborators> collaborators = collabRepository.findByBoard(board);

        return collaborators.stream()
                .map(collab -> modelMapper.map(collab, ShowCollabDTO.class))
                .collect(Collectors.toList());
    }

    public ShowCollabDTO getCollabByOid(String boardId, String Oid){

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));

        UserLocal user = userLocalRepository.findByOid(Oid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Collaborators collaborators = collabRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborator not found"));

        return modelMapper.map(collaborators, ShowCollabDTO.class);
    }

    public boolean isCollaborator(String userOid, String boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found"));

        if (board.getOwner().getOid().equals(userOid)) {
            return true;
        }

        // Check if user is collaborator
        UserLocal user = userLocalRepository.findByOid(userOid)
                .orElse(null);
        if (user == null) {
            return false;
        }

        Optional<Collaborators> collaborators = collabRepository.findByBoardAndUser(board, user);
        return collaborators.isPresent();
    }

    public ShowCollabDTO addCollaborator(String boardId, String email , String access_right, String oid) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("board not found"));

        Collaborators existingCollaborator = collabRepository.findByUser_EmailAndBoard_BoardId(email, boardId);
        if (existingCollaborator != null) {
            throw new ItemNotFoundException("Collaborator already exists");
        }

        if (email == null || access_right == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and access_right are required");
        }

        System.out.println(email);
        AccessRight accessRight;
        try {
            accessRight = AccessRight.valueOf(access_right.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid access_right value");
        }

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in system"));

        UserLocal userLocal = userLocalRepository.findByEmail(email)
                .orElseGet(() -> {
                  UserLocal newUserLocal = new UserLocal();
                  newUserLocal.setOid(user.getOid());
                  newUserLocal.setName(user.getName());
                  newUserLocal.setUsername(user.getUsername());
                  newUserLocal.setEmail(user.getEmail());
                  return userLocalRepository.save(newUserLocal);
                });

        if (userLocal.getOid().equals(oid)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot add yourself as a collaborator");
        }

        if (collabRepository.findByBoardAndUser(board, userLocal).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a collaborator");
        }

        // Create and save the new collaborator
        Collaborators newCollaborator = new Collaborators();
        BoardCollabId id = new BoardCollabId(boardId, userLocal.getOid());
        newCollaborator.setId(id);
        newCollaborator.setBoard(board);
        newCollaborator.setUser(userLocal);
        newCollaborator.setEmail(email);
        newCollaborator.setAccessRight(accessRight);
        newCollaborator.setName(userLocal.getName());

        Collaborators savedCollaborator = collabRepository.save(newCollaborator);
        return modelMapper.map(savedCollaborator,ShowCollabDTO.class);
    }

    public List<Collaborators> getCollabsByOnlyOid(String oid){
        return collabRepository.findByUser_Oid(oid);
    }


}
