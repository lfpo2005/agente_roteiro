package br.com.devluisoliveira.agenteroteiro.core.port.in.dto;

/*
* Class de erroDtopara documentação do swagger
* */

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto{

    private String type;

    private String title;

    private int status;

    private String detail;

    private String instance;

}
