package br.com.devluisoliveira.agenteroteiro.core.application.service.enums;

import lombok.Getter;

/**
 * Enum que define os tempos padrão para roteiros
 */
@Getter
public enum DurationType {

    SECONDS_30("30 segundos", 30, "curto"),
    SECONDS_60("1 minuto", 60, "curto"),
    MINUTES_3("3 minutos", 180, "curto"),
    MINUTES_5("5 minutos", 300, "curto"),
    MINUTES_10("10 minutos", 600, "médio"),
    MINUTES_15("15 minutos", 900, "médio"),
    MINUTES_20("20 minutos", 1200, "médio"),
    MINUTES_25("25 minutos", 1500, "longo"),
    MINUTES_30("30 minutos", 1800, "longo");

    private final String displayName;
    private final int durationInSeconds;
    private final String category;

    DurationType(String displayName, int durationInSeconds, String category) {
        this.displayName = displayName;
        this.durationInSeconds = durationInSeconds;
        this.category = category;
    }

    /**
     * Retorna a duração em minutos
     * @return Duração em minutos (arredondado para cima)
     */
    public int getDurationInMinutes() {
        return (int) Math.ceil(durationInSeconds / 60.0);
    }

    /**
     * Encontra o tipo de duração pelo tempo em minutos mais próximo
     * @param minutes Tempo em minutos
     * @return Tipo de duração mais próximo
     */
    public static DurationType findClosest(int minutes) {
        int seconds = minutes * 60;
        DurationType closest = MINUTES_10; // valor padrão
        int minDifference = Integer.MAX_VALUE;

        for (DurationType type : values()) {
            int difference = Math.abs(type.durationInSeconds - seconds);
            if (difference < minDifference) {
                minDifference = difference;
                closest = type;
            }
        }

        return closest;
    }

    /**
     * Retorna todos os tipos de duração de uma categoria
     * @param category Categoria (curto, médio, longo)
     * @return Array de tipos de duração da categoria
     */
    public static DurationType[] getByCategory(String category) {
        return java.util.Arrays.stream(values())
                .filter(type -> type.category.equals(category))
                .toArray(DurationType[]::new);
    }

    /**
     * Retorna o tempo estimado de palavras com base na duração
     * Assumindo uma média de 130 palavras por minuto para português brasileiro
     * @return Número aproximado de palavras
     */
    public int getEstimatedWordCount() {
        // Taxa média de fala em português: ~130 palavras por minuto
        return (int)(getDurationInMinutes() * 130);
    }

    /**
     * Retorna o tempo estimado de caracteres com base na duração
     * Assumindo uma média de 750 caracteres por minuto para português brasileiro
     * @return Número aproximado de caracteres
     */
    public int getEstimatedCharacterCount() {
        // Taxa média para português: ~750 caracteres por minuto
        return getDurationInMinutes() * 750;
    }
}