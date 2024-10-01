package com.example.int221backend.services;

import com.example.int221backend.dtos.AddBoardDTO;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.bouncycastle.math.raw.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Board> getAllBoard(String oid){
        return boardRepository.findByOwner_Oid(oid);
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

}
