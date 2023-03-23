package com.example.pieona.jwt;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Token {

    @Id @JsonIgnore
    private Long id;

    private String refresh_token;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiration;

    public void setExpiration(Long expiration){
        this.expiration = expiration;
    }


}
