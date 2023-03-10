package com.example.pieona.board.dto;

import com.example.pieona.board.entity.Board;
import com.example.pieona.user.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardRequestDto {

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

    public Board toEntity(){
        return Board.builder()
                .myTalent(myTalent)
                .myGender(myGender)
                .location(location)
                .preferTalent(preferTalent)
                .preferGender(preferGender)
                .lessonType(lessonType)
                .content(content)
                .build();
    }

}
