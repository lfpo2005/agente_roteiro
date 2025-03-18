package br.com.devluisoliveira.agenteroteiro.shared.validation;

import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))})
})
public @interface CustomOperation {
    String summary() default "";
}
