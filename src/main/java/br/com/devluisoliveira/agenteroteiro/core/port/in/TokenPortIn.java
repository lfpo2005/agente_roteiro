package br.com.devluisoliveira.agenteroteiro.core.port.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenPortIn {

    private static final String TOKEN_KEY = "authToken";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveToken(String token) {
        redisTemplate.opsForValue().set(TOKEN_KEY, token, 1, TimeUnit.MINUTES);
        log.info("Token salvo: {}", token);
    }

    public String getToken() {
        String token = redisTemplate.opsForValue().get(TOKEN_KEY);
        if (token == null) {
            log.error("Token não encontrado");
            return null; 
        }
        log.info("Token recuperado: {}", token);
        return token;
    }
}
