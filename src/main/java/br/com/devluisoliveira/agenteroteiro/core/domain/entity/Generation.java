package br.com.devluisoliveira.agenteroteiro.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_GENERATION")
public class Generation {

    @Id
    private UUID processId;
    private String theme;
    private String prayerStyle;
    private String duration;
    private String prayerType;
    private String language;
    private String title;
    private String notes;
    private Boolean generateShortVersion;
    private Boolean generateAudio;
}
