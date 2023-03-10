package com.example.pieona.board.repo;

import com.example.pieona.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAll();

}
