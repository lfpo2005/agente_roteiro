package br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.ContentGeneration;

public interface ContentGenerationPortOut {

    ContentGeneration saveContentGeneration(ContentGeneration contentGeneration);
}
