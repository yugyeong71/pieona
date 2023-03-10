package com.example.pieona.board.service;

import com.example.pieona.board.dto.BoardRequestDto;
import com.example.pieona.board.entity.Board;
import com.example.pieona.board.repo.BoardRepository;
import com.example.pieona.common.SecurityUtil;
import com.example.pieona.common.SuccessMessage;
import com.example.pieona.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    public SuccessMessage uploadBoard(BoardRequestDto requestDto) {
        Board board = requestDto.toEntity();

        board.boardWriter(userRepository.findByEmail(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new BadCredentialsException("잘못된 정보입니다.")));

        boardRepository.save(board);

        return new SuccessMessage();
    }

    public SuccessMessage deleteBoard(BoardRequestDto requestDto, Long id) {
        Board board = requestDto.toEntity();
        board.listBoard(userRepository.findByEmail(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new BadCredentialsException("잘못된 정보입니다.")));

        boardRepository.deleteById(id);

        return new SuccessMessage();
    }




}
