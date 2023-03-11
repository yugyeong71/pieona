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
@RedisHash("refreshToken")
@AllArgsConstructor @NoArgsConstructor
public class Token {

    @Id @JsonIgnore
    private Long id;

    private String refresh_token;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Integer expiration;

    public void setExpiration(Integer expiration){
        this.expiration = expiration;
    }


}
