package br.com.devluisoliveira.agenteroteiro.core.application.service.enums;

import lombok.Getter;

@Getter
public enum ContentType {
    TITLE("Título do Vídeo", "Gera títulos chamativos para atrair cliques"),
    DESCRIPTION("Descrição do Vídeo", "Gera descrições otimizadas para SEO"),
    TAGS("Tags", "Gera tags relevantes para melhorar a descoberta"),
    SCRIPT("Roteiro", "Gera roteiro completo para o vídeo"),
    THUMBNAIL_IDEA("Ideia para Thumbnail", "Gera sugestões de thumbnail"),
    AUDIO_SCRIPT("Script para Áudio", "Gera texto otimizado para narração"),
    TRANSCRIPTION("Transcrição", "Gera transcrição a partir do áudio"),
    SHORTS_IDEA("Ideia para Shorts", "Gera ideias para clips curtos");

    private final String label;
    private final String description;

    ContentType(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
