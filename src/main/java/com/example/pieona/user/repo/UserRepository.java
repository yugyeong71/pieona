package com.example.pieona.user.repo;

import com.example.pieona.oauth2.SocialType;
import com.example.pieona.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // 사용자 조회

    boolean existsByEmail(String email); // 아이디 중복 확인

    boolean existsByNickname(String string); // 닉네임 중복 확인

    Optional<User> deleteByEmail(String email);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

}
