package br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar opções de tipos e estilos de oração
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrayerOptionDto {

    /**
     * Nome técnico (constante do enum)
     */
    private String name;

    /**
     * Nome amigável para exibição ao usuário
     */
    private String displayName;

    /**
     * Descrição detalhada
     */
    private String description;

    /**
     * Palavras-chave relacionadas
     */
    private String keywords;

    /**
     * Tipo de opção (PRAYER_TYPE ou PRAYER_STYLE)
     */
    private String optionType;

    /**
     * Retorna um array de palavras-chave individuais
     */
    public String[] getKeywordArray() {
        if (keywords == null || keywords.trim().isEmpty()) {
            return new String[0];
        }
        return keywords.split("\\s*,\\s*");
    }
}
