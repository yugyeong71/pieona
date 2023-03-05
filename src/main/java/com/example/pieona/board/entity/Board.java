package com.example.pieona.board.entity;

import com.example.pieona.board.dto.Location;
import com.example.pieona.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    private String myTalent;

    private String myGender;

    //private String myLocation;

    @Embedded
    private Location location;

    private String preferTalent;

    private String preferGender;

    private String lessonType;

    private String content;

    public void boardWriter(User userId) {
        //writer는 변경이 불가능하므로 이렇게만 해주어도 될듯
        this.userId = userId;
        userId.addBoard(this);
    }

}
