package br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar opções de duração no frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DurationOptionDto {


    private String name;

    private String displayName;

    private int durationInSeconds;

    private int durationInMinutes;

    private String category;

    private int estimatedWordCount;

    private int estimatedCharacterCount;
}