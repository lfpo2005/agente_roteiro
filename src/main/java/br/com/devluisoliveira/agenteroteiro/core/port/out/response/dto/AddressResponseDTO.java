package br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponseDTO {

    private String street;
    private String number;
    private String city;
    private String state;
    private String zipCode;
    private String complement;
    private String neighborhood;
    private String type;
}
