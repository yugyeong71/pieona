package com.example.pieona.board.entity;

import com.example.pieona.board.dto.Location;
import com.example.pieona.user.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userId")
    @ManyToOne
    private User userId;

    private String myTalent;

    private String myGender;

    @Embedded
    private Location location;

    private String preferTalent;

    private String preferGender;

    private String lessonType;

    private String content;

    public void boardWriter(User userId) {
        this.userId = userId;
        userId.addBoard(this);
    }

    public void listBoard(User userId) {
        this.userId = userId;
    }

    public void update(String myTalent, Location location, String preferTalent, String preferGender, String lessonType, String content){
        this.myTalent = myTalent;
        this.location = location;
        this.preferTalent = preferTalent;
        this.preferGender = preferGender;
        this.lessonType = lessonType;
        this.content = content;
    }


}
