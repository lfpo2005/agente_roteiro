package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenerationRequestDto {

    private String processId;
    private String theme;
    private String prayerStyle;
    private String duration;
    private String prayerType;
    private String language = "es_MX";
    private String title;
    private String notes;
    private Boolean generateShortVersion = false;
    private Boolean generateAudio = false;

}
