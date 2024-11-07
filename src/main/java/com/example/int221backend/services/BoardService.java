package com.example.int221backend.services;

import com.example.int221backend.dtos.AddBoardDTO;
import com.example.int221backend.dtos.BoardDTO;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.CollabRepository;
import com.example.int221backend.repositories.local.UserLocalRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.bouncycastle.math.raw.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollabRepository collabRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserLocalRepository userLocalRepository;

    @Autowired
    private JwtService jwtService;

    public List<BoardDTO> getAllBoard(String token) {
        if (token == null) {
            return boardRepository.findByVisibility(BoardVisi.PUBLIC)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        String userOid = jwtService.getOidFromToken(token);
        UserLocal user = userLocalRepository.findById(userOid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Set<Board> userBoards = new HashSet<>(boardRepository.findByOwner(user));
        Set<Board> publicBoards = new HashSet<>(boardRepository.findByVisibility(BoardVisi.PUBLIC));
        Set<Board> collaborationBoards = collabRepository.findByUser(user)
                .stream()
                .filter(collab -> collab.getAccessRight() == AccessRight.WRITE
                        || collab.getAccessRight() == AccessRight.READ)
                .map(Collaborators::getBoard)
                .collect(Collectors.toSet());

        Set<Board> allBoards = new HashSet<>();
        allBoards.addAll(userBoards);
        allBoards.addAll(publicBoards);
        allBoards.addAll(collaborationBoards);

        return allBoards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Board addBoard(Board newBoard) {
        if (newBoard == null || newBoard.getName() == null || newBoard.getName().isEmpty()){
            throw new BadRequestException("boardName is null, empty");
        }

        if (newBoard.getName().length() > 120){
            throw new BadRequestException("boardName is longer than limit");
        }

        if (newBoard.getVisibility() == null || newBoard.getVisibility().toString().isEmpty()){
            newBoard.setVisibility(BoardVisi.PRIVATE);
        } else if (newBoard.getVisibility() != null &&
                ("private".equalsIgnoreCase(newBoard.getVisibility().toString()) ||
                "public".equalsIgnoreCase(newBoard.getVisibility().toString()))
            ) {
            newBoard.setVisibility(newBoard.getVisibility());
        } else {
            throw new BadRequestException("visibility should be public or private");
        }
        return boardRepository.save(newBoard);
    }

    public Board getBoardByBoardId(String boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Board ID " + boardId + " does not exist"));
    }


    public boolean existsById(String boardId) {
        return boardRepository.existsById(boardId);
    }

    private BoardDTO convertToDTO(Board board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getBoardId());
        dto.setName(board.getName());
        dto.setVisibility(board.getVisibility().toString().toLowerCase());

        // Set owner information
        BoardDTO.PMUserDTO ownerDTO = new BoardDTO.PMUserDTO();
        ownerDTO.setOid(board.getOwner().getOid());
        ownerDTO.setName(board.getOwner().getName());
        dto.setOwner(ownerDTO);

        // Get and set collaborators
        List<Collaborators> collaborators = collabRepository.findByBoard(board);
        List<BoardDTO.CollaboratorDTO> collaboratorDTOs = collaborators.stream()
                .map(collab -> {
                    BoardDTO.CollaboratorDTO collabDTO = new BoardDTO.CollaboratorDTO();
                    collabDTO.setOid(collab.getUser().getOid());
                    collabDTO.setName(collab.getUser().getName());
                    collabDTO.setAccess_right(collab.getAccessRight().toString());
                    return collabDTO;
                })
                .collect(Collectors.toList());
        dto.setCollaborators(collaboratorDTOs);

        return dto;
    }


}
