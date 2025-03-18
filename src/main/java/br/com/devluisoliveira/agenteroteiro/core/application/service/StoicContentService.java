package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.StoicContentMapper;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.StoicContentPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoicContentService  {

    private final StoicContentMapper stoicContentMapper;


    @Override
    public ContentGenerationResponse generateContent(User user, StoicContentGenerationRequest request) {
        log.info("[StoicContentService.generateContent] - Iniciando a geração de conteúdo");

        // Converte a requisição específica para o formato padrão
        ContentGenerationRequest standardRequest = stoicContentMapper.convertToStandardRequest(request);


        return null;
    }
}
