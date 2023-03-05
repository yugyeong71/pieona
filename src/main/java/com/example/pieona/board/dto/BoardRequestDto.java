package com.example.pieona.board.dto;

import com.example.pieona.board.entity.Board;
import com.example.pieona.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BoardRequestDto {

    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    private String myTalent;

    private String myGender;

    //private String myLocation;

    private Location location;

    private String preferTalent;

    private String preferGender;

    private String lessonType;

    private String content;

    public Board toEntity(){
        return Board.builder()
                .myTalent(myTalent)
                .myGender(myGender)
                //.myLocation(myLocation)
                .location(location)
                .preferTalent(preferTalent)
                .preferGender(preferGender)
                .lessonType(lessonType)
                .content(content)
                .build();
    }

}
