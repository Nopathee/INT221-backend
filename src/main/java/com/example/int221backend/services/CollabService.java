package com.example.int221backend.services;

import com.example.int221backend.dtos.ListShowCollabDTO;
import com.example.int221backend.dtos.ShowCollabDTO;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.entities.shared.User;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.exception.ConflictException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.CollabRepository;
import com.example.int221backend.repositories.local.UserLocalRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
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

    @Autowired
    private UserDetailsService userDetailsService;

    public ListShowCollabDTO getAllCollab(String boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("Board not found!"));
        List<Collaborators> collaborators = collabRepository.findByBoard(board);

        List<ShowCollabDTO> collabList = collaborators.stream()
                .map(collaborator -> new ShowCollabDTO(
                        collaborator.getUser().getOid(),
                        collaborator.getUser().getName(),
                        collaborator.getUser().getEmail(),
                        collaborator.getAccessRight(),
                        collaborator.getAddedOn()))
                .collect(Collectors.toList());

        ListShowCollabDTO listShowCollabDTO = new ListShowCollabDTO();
        listShowCollabDTO.setShowCollabDTOS(collabList);

        return listShowCollabDTO;
    }

    public ShowCollabDTO getCollabByOid(String boardId, String oid){
        Collaborators collaborator = collabRepository.findByUser_OidAndBoard_BoardId(oid,boardId);
        if (collaborator == null) {
            throw new ItemNotFoundException("Collaborator not found!");
        }

        return new ShowCollabDTO(
                collaborator.getUser().getOid(),
                collaborator.getUser().getName(),
                collaborator.getUser().getEmail(),
                collaborator.getAccessRight(),
                collaborator.getAddedOn()
        );

    }



    public ShowCollabDTO addCollaborator(String boardId, String email , String access_right) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new ItemNotFoundException("board not found"));

        UserDetails userDetails = userDetailsService.loadUserByEmail(email.trim());
        UserLocal user = userLocalRepository.findByUsername(userDetails.getUsername());

        if (user == null){
            throw new ItemNotFoundException("User not found!");
        }

        if (isCollaborator(email,boardId)){
            throw new ConflictException("The user is already a collaborator of this board.");
        }

        Collaborators collaborators = new Collaborators();
        collaborators.setBoard(board);
        collaborators.setUser(user);
        collaborators.setAccessRight(AccessRight.valueOf(access_right.toUpperCase()));
        collaborators.setAddedOn(ZonedDateTime.now());

        collabRepository.save(collaborators);

        return new ShowCollabDTO(
                user.getOid(),
                user.getName(),
                user.getEmail(),
                collaborators.getAccessRight(),
                collaborators.getAddedOn()
        );
    }

    public ShowCollabDTO editCollaborator(String boardId, String oid , String accessRight){
        Collaborators collaborators = collabRepository.findByUser_OidAndBoard_BoardId(oid,boardId);

        if (collaborators == null){
            throw new ItemNotFoundException("collaborator not found!");
        }

        collaborators.setAccessRight(AccessRight.valueOf(accessRight.toUpperCase()));

        collabRepository.save(collaborators);

        return new ShowCollabDTO(
                collaborators.getUser().getOid(),
                collaborators.getUser().getName(),
                collaborators.getUser().getEmail(),
                collaborators.getAccessRight(),
                collaborators.getAddedOn()
        );
    }

    public void deleteCollab(String oid,String boardId){
        Collaborators collaborators = collabRepository.findByUser_OidAndBoard_BoardId(oid, boardId);
        if (collaborators == null){
            throw new ItemNotFoundException("this user is not board collaborators");
        }

        collabRepository.delete(collaborators);
    }

    public boolean isCollaborator(String email, String boardId) {

        Collaborators collaborator = collabRepository.findByBoardIdAndUserEmail(boardId, email);
        return collaborator != null;
    }

    public Collaborators findCollaboratorByUserIdAndBoardId(String userId, String boardId) {

        return collabRepository.findByBoard_BoardId(boardId).stream()
                .filter(collab -> collab.getUser().getOid().equals(userId))
                .findFirst()
                .orElse(null); // คืนค่า null ถ้าไม่พบ
    }

}
