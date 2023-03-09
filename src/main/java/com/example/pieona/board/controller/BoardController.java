package com.example.pieona.board.controller;

import com.example.pieona.board.dto.BoardRequestDto;
import com.example.pieona.board.entity.Board;
import com.example.pieona.board.repo.BoardRepository;
import com.example.pieona.board.service.BoardService;
import com.example.pieona.common.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final BoardRepository boardRepository;

    @PostMapping("/upload")
    public ResponseEntity<SuccessMessage> uploadBoard(@RequestBody BoardRequestDto requestDto){
        return new ResponseEntity<>(boardService.uploadBoard(requestDto), HttpStatus.OK);
    }

    @DeleteMapping("/post/{id}")
    public SuccessMessage delBoard(@PathVariable Long id, BoardRequestDto requestDto){
        boardService.deleteBoard(requestDto,id);
        return new SuccessMessage();
    }
}
