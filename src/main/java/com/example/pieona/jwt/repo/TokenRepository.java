package com.example.pieona.jwt.repo;

import com.example.pieona.jwt.Token;
import org.springframework.data.repository.CrudRepository;


public interface TokenRepository extends CrudRepository<Token, Long> {

    // CrudRepository를 확장하면 save, findBy 등등을 사용할 수 있따.

}
