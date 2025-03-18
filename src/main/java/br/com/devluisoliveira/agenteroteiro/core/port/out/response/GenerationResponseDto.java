package br.com.devluisoliveira.agenteroteiro.core.port.out.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerationResponseDto {
    private String processId;
    private String message;
}
