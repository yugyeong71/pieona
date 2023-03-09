package com.example.pieona.entity;

import com.example.pieona.board.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    private String gender;

    private String image;

    private boolean isOn;

    private String refreshToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    public void setRoles(List<Authority> role) {
        this.roles = role;
        role.forEach(o -> o.setUser(this));
    }

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    // 회원탈퇴 -> 작성한 게시물 모두 삭제
    @OneToMany(mappedBy = "userId", cascade = ALL, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();


    // 연관관계 메소드
    public void addBoard(Board board){
        boardList.add(board);
    }

}
