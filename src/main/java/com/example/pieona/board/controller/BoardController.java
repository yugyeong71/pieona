package com.example.pieona.board.controller;

import com.example.pieona.board.dto.BoardRequestDto;
import com.example.pieona.board.service.BoardService;
import com.example.pieona.common.SuccessMessage;
import com.example.pieona.dto.SignRequest;
import com.example.pieona.dto.SignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/upload")
    public ResponseEntity<SuccessMessage> uploadBoard(@RequestBody BoardRequestDto requestDto){
        return new ResponseEntity<>(boardService.uploadBoard(requestDto), HttpStatus.OK);
    }
}
