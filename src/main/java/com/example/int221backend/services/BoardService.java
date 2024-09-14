package com.example.int221backend.services;

import com.example.int221backend.entities.Board;
import com.example.int221backend.entities.BoardRepository;
import com.example.int221backend.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Board> getAllBoard(){
        return boardRepository.findAll();
    }

    public String getBoardForUser(String userId) {
        Board board = boardRepository.findByOwner_Oid(userId);
        if (board != null){
            return board.getBoardId();
        }
        return null;
    }



//    @Transactional
//    public Board createBoardForUser(Integer userId, String boardName) throws NotCreatedException {
//        if (boardName == null || boardName.isEmpty() || boardName.length() > 100) {
//            throw new NotCreatedException("Invalid board name");
//        }
//
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new NotCreatedException("User not found"));
//
//        Board newBoard = new Board();
//        newBoard.setBoardId(generateNanoId());
//        newBoard.setOwner(owner);
//        newBoard.setName(boardName);
//
//        return boardRepository.save(newBoard);
//    }
}
