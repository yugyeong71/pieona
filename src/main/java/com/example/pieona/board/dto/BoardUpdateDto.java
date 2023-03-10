package com.example.pieona.board.dto;

import com.example.pieona.board.entity.Board;
import com.example.pieona.user.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateDto {

    private String myTalent;

    private Location location;

    private String preferTalent;

    private String preferGender;

    private String lessonType;

    private String content;



}
