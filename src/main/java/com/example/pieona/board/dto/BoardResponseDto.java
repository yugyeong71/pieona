package com.example.pieona.board.dto;

import com.example.pieona.board.entity.Board;
import com.example.pieona.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.stream.Collectors;

@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class BoardResponseDto {

    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    private String myTalent;

    private String myGender;

    private Location location;

    private String preferTalent;

    private String preferGender;

    private String lessonType;

    private String content;


}
