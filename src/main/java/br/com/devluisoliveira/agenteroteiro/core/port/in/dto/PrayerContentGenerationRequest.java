package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.DurationType;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrayerContentGenerationRequest extends ContentGenerationRequest {

     private PrayerType prayerType;

    private PrayerStyle prayerStyle;

    private String biblePassage;

    private String prayerTheme;

    private String targetAudience;

    private String occasion;

    private String personalizationName;

    private String bibleVersion = "NVI";

    private Boolean includeInstructions = true;

    public PrayerContentGenerationRequest() {

    }

//    @Override
//    public DurationType getDurationType() {
//        DurationType type = super.getDurationType();
//        return type != null ? type : DurationType.MINUTES_5;
//    }

    public PrayerContentGenerationRequest(PrayerType prayerType, PrayerStyle prayerStyle, String prayerTheme) {
        this.prayerType = prayerType;
        this.prayerStyle = prayerStyle;
        this.prayerTheme = prayerTheme;
    }
}
