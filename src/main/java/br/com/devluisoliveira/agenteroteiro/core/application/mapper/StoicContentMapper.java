package br.com.devluisoliveira.agenteroteiro.core.application.mapper;

import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StoicContentMapper {


    public Map<String, Object> convertRequestToMap(StoicContentGenerationRequest request) {
        Map<String, Object> requestMap = new HashMap<>();

        // Mapear propriedades básicas
        requestMap.put("processId", request.getProcessId());
        requestMap.put("title", request.getTitle());
        requestMap.put("theme", request.getTheme());
        requestMap.put("notes", request.getNotes());
        requestMap.put("targetDuration", request.getTargetDuration());
        requestMap.put("language", request.getLanguage());
        requestMap.put("contentTypes", request.getContentTypes());
        requestMap.put("generateShortVersion", request.getGenerateShortVersion());
        requestMap.put("generateAudio", request.getGenerateAudio());
        requestMap.put("voiceType", request.getVoiceType());

        // Mapear propriedades específicas de conteúdo estoico
        requestMap.put("philosopherName", request.getPhilosopherName());
        requestMap.put("philosopherStyle", request.getPhilosopherStyle());
        requestMap.put("stoicConcept", request.getStoicConcept());
        requestMap.put("practicalApplication", request.getPracticalApplication());
        requestMap.put("additionalContext", request.getAdditionalContext());

        return requestMap;
    }
}
