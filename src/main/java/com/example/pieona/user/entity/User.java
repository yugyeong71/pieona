package com.example.pieona.user.entity;

import com.example.pieona.board.entity.Board;
import com.example.pieona.user.Role;
import com.example.pieona.oauth2.SocialType;
import com.example.pieona.user.dto.UpdateUserDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

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

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

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
    @OneToMany(mappedBy = "userId", cascade = ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Board> boardList = new ArrayList<>();


    // 연관관계 메소드
    public void addBoard(Board board){
        boardList.add(board);
    }

    public void update(UpdateUserDto dto) {
        if (dto.getNickname() != null) this.nickname = dto.getNickname();
        if (dto.getImage() != null) this.image = dto.getImage();
    }

    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
        return passwordEncoder.matches(checkPassword, getPassword());
    }

    public void oauth2Update(UpdateUserDto dto){
        this.nickname = dto.getNickname();
        this.image = dto.getImage();
        this.gender = dto.getGender();
    }

}
