package com.example.pieona.board.dto;

import com.example.pieona.user.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

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
