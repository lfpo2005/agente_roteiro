package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.PhilosopherType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StoicContentGenerationRequest extends ContentGenerationRequest {

    // Filósofo estoico selecionado
    private PhilosopherType philosopher;
    private String philosopherName;
    private String philosopherStyle;

    // Parâmetros específicos de estoicismo
    private String stoicConcept; // Ex: Dicotomia de controle, Virtude, Cosmopolitismo
    private String practicalApplication; // Ex: Lidar com adversidade, Gestão emocional

    // Parâmetros padrão
    private String targetAudience;
    private String toneStyle = "Filosófico com aplicações práticas";
    private Integer targetDuration = 15;
}
