package br.com.devluisoliveira.agenteroteiro.core.port.in;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;

public interface StoicContentPortIn {
    ContentGenerationResponse generateContent(User user, StoicContentGenerationRequest request);
}
