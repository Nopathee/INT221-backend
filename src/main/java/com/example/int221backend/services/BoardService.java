package com.example.int221backend.services;

import com.example.int221backend.dtos.AddBoardDTO;
import com.example.int221backend.dtos.BoardDTO;
import com.example.int221backend.dtos.UserLocalResponse;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.SharedBoard;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.CollabRepository;
import com.example.int221backend.repositories.local.SharedBoardRepository;
import com.example.int221backend.repositories.local.UserLocalRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.bouncycastle.math.raw.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private SharedBoardRepository sharedBoardRepository;

//    public List<BoardDTO> getAllBoard(String token) {
//        if (token == null) {
//            return boardRepository.findByVisibility(BoardVisi.PUBLIC)
//                    .stream()
//                    .map(this::convertToDTO)
//                    .collect(Collectors.toList());
//        }
//
//        String userOid = jwtService.getOidFromToken(token);
//        UserLocal user = userLocalRepository.findById(userOid)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
//
//        Set<Board> userBoards = new HashSet<>(boardRepository.findByOwner(user));
//        Set<Board> publicBoards = new HashSet<>(boardRepository.findByVisibility(BoardVisi.PUBLIC));
//        Set<Board> collaborationBoards = collabRepository.findByUser(user)
//                .stream()
//                .filter(collab -> collab.getAccessRight() == AccessRight.WRITE
//                        || collab.getAccessRight() == AccessRight.READ)
//                .map(Collaborators::getBoard)
//                .collect(Collectors.toSet());
//
//        Set<Board> allBoards = new HashSet<>();
//        allBoards.addAll(userBoards);
//        allBoards.addAll(publicBoards);
//        allBoards.addAll(collaborationBoards);
//
//        return allBoards.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

    public Map<String, List<BoardDTO>> getPersonalAndCollabBoards(String oId){

        List<Board> ownBoards = boardRepository.findAllByOwner_Oid(oId);
        List<BoardDTO> personalBoards = ownBoards.stream()
                .map(board -> convertToDTO(board, "OWNER"))
                .collect(Collectors.toList());

        List<Collaborators> collaborators = collabRepository.findAllByUser_Oid(oId);
        List<BoardDTO> boardDTOS = collaborators.stream()
                .map(collaborator -> convertToDTO(collaborator.getBoard(), collaborator.getAccessRight().name()))
                .collect(Collectors.toList());

        Map<String, List<BoardDTO>> boardDTOList = new HashMap<>();
        boardDTOList.put("personalBoards", personalBoards);
        boardDTOList.put("collabBoards", boardDTOS);

        return boardDTOList;
    }

    @Transactional("projectManagementTransactionManager")
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

    public BoardDTO getBoardByBoardId(String boardId) {
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (!optionalBoard.isPresent()){
            throw new ItemNotFoundException("Board not found!");
        }

        Board board = optionalBoard.get();

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(board.getBoardId());
        boardDTO.setBoardName(board.getName());
        boardDTO.setVisibility(board.getVisibility().toString());

        SharedBoard sharedBoard = sharedBoardRepository.findByBoard(board);
        if (sharedBoard != null && sharedBoard.getOwner() != null){
            UserLocal owner = sharedBoard.getOwner();
            UserLocalResponse ownerRes = new UserLocalResponse();
            ownerRes.setUserId(owner.getOid());
            ownerRes.setUsername(owner.getUsername());
            ownerRes.setName(owner.getName());
            ownerRes.setEmail(owner.getEmail());

            boardDTO.setOwner(ownerRes);
        } else {
            throw new BadRequestException("Invalid Board");
        }

        return boardDTO;
    }


    public boolean existsById(String boardId) {
        return boardRepository.existsById(boardId);
    }

    private BoardDTO convertToDTO(Board board, String accessRight) {
        BoardDTO dto = new BoardDTO();

        dto.setId(board.getBoardId());
        dto.setBoardName(board.getName());
        dto.setVisibility(board.getVisibility().toString().toLowerCase());

        SharedBoard sharedBoard = sharedBoardRepository.findByBoard(board);
        if (sharedBoard != null && sharedBoard.getOwner() != null) {
            UserLocal owner = sharedBoard.getOwner();
            UserLocalResponse ownerResponse = new UserLocalResponse();
            ownerResponse.setUserId(owner.getOid());
            ownerResponse.setUsername(owner.getUsername());
            ownerResponse.setName(owner.getName());
            ownerResponse.setEmail(owner.getEmail());

            dto.setOwner(ownerResponse);
        } else {
            dto.setOwner(null);
        }

        if (accessRight != null){
            dto.setAccessRight(accessRight);
        }

        return dto;
    }

    @Transactional("projectManagementTransactionManager")
    public Board updateBoard(BoardDTO boardDTO) {

        Board boardEntity = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ItemNotFoundException("Board not found"));

        boardEntity.setName(boardDTO.getBoardName());
        boardEntity.setVisibility(BoardVisi.valueOf(boardDTO.getVisibility().toUpperCase()));

        // บันทึก entity หลังจากทำการอัปเดต
        return boardRepository.save(boardEntity);
    }

    @Transactional("projectManagementTransactionManager")
    public Board getMainBoardById(String boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found with id: " + boardId));
    }


}
