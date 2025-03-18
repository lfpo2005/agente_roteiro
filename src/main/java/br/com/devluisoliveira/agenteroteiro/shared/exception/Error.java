package br.com.devluisoliveira.agenteroteiro.shared.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "reason")
    private String reason;

    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "status")
    private String status;
    }
