package br.com.devluisoliveira.agenteroteiro.shared.configs;

import br.com.devluisoliveira.agenteroteiro.core.application.service.MinioService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private final MinioService minioService;

    public MinioConfig(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostConstruct
    public void initialize() {
        minioService.createBucket();
    }
}

