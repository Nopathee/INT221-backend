package com.example.int221backend.services;

import com.example.int221backend.dtos.ShowCollabDTO;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.CollabRepository;
import com.example.int221backend.repositories.local.UserLocalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private ModelMapper modelMapper;

    public List<ShowCollabDTO> getAllCollab(String boardId) {
        if (!boardRepository.existsById(boardId)){
            throw new ItemNotFoundException("board not found");
        }

        List<Collaborators> collaborators = collabRepository.findByBoard_BoardId(boardId);

        return collaborators.stream()
                .map(collaborator -> {
                    ShowCollabDTO dto = modelMapper.map(collaborator, ShowCollabDTO.class);
                    dto.setOid(collaborator.getUser().getOid());
                    dto.setName(collaborator.getUser().getName());
                    dto.setEmail(collaborator.getUser().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ShowCollabDTO getCollabByOid(String boardId, String Oid){

        Collaborators collaborators = collabRepository.findByUser_OidAndBoard_BoardId(Oid,boardId);
        if (collaborators == null){
            throw  new ItemNotFoundException("collaborator not found");
        }

        ShowCollabDTO showCollabDTO = modelMapper.map(collaborators, ShowCollabDTO.class);
        showCollabDTO.setOid(collaborators.getUser().getOid());
        showCollabDTO.setName(collaborators.getUser().getName());
        showCollabDTO.setEmail(collaborators.getUser().getEmail());
        return showCollabDTO;
    }

    public boolean isCollaborator(String userOid, String boardId) {
        Collaborators collaborator = collabRepository.findByUser_OidAndBoard_BoardId(userOid, boardId);
        return collaborator != null; // Returns true if a collaborator exists
    }

    public ShowCollabDTO addCollaborator(String boardId, String email , AccessRight access_right) {
        // Check if the email already exists in collaborators
        Collaborators existingCollaborator = collabRepository.findByUser_EmailAndBoard_BoardId(email, boardId);
        if (existingCollaborator != null) {
            throw new ItemNotFoundException("Collaborator already exists");
        }

        // Find the user by email
        UserLocal user = userLocalRepository.findByEmail(email);
        if (user == null) {
            throw new ItemNotFoundException("User not found");
        }

        // Create and save the new collaborator
        Collaborators newCollaborator = new Collaborators();
        newCollaborator.setUser(user);
        newCollaborator.setBoard(boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found")));
        newCollaborator.setAccessRight(access_right);

        collabRepository.save(newCollaborator);

        // Map to DTO
        ShowCollabDTO showCollabDTO = modelMapper.map(newCollaborator, ShowCollabDTO.class);
        showCollabDTO.setOid(user.getOid());
        showCollabDTO.setName(user.getName());
        showCollabDTO.setEmail(user.getEmail());
        showCollabDTO.setAccess_right(access_right);
        return showCollabDTO;
    }




}
