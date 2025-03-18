package br.com.devluisoliveira.agenteroteiro.core.port.in;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;

public interface GenericGenerationPortIn {
    ContentGenerationResponse generateContent(User user, ContentGenerationRequest request);

}
