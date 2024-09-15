package com.example.int221backend.services;

import com.example.int221backend.dtos.AddBoardDTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import org.bouncycastle.math.raw.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return boardRepository.save(newBoard);
    }

    public Board getBoardByBoardId(String boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board ID " + boardId + " does not exist"));
    }

    public boolean existsById(String boardId) {
        return boardRepository.existsById(boardId);
    }

}
