package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgentPrayerRequestContent extends ContentGenerationRequest {

    private String prayerStyle;
    private String prayerType;

}
