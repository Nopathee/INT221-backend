package com.example.int221backend.user_services;

import com.example.int221backend.exception.NotCreatedException;
import com.example.int221backend.user_entities.Board;
import com.example.int221backend.user_entities.BoardRepository;
import com.example.int221backend.user_entities.User;
import com.example.int221backend.user_entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public Board getBoardForUser(String userId) {
        return boardRepository.findByOwner_Oid(userId);
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
