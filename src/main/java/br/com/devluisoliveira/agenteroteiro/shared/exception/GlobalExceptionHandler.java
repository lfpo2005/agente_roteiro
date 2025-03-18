package br.com.devluisoliveira.agenteroteiro.shared.exception;

import br.com.devluisoliveira.agenteroteiro.shared.exception.helper.GenericErrorsEnum;
import br.com.devluisoliveira.agenteroteiro.shared.exception.helper.MessagesEnum;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Error> noHandlerFoundException(NoHandlerFoundException e) {
        log.error(e.getClass().getSimpleName(), e);

        String errorReason = "Caminho '" + e.getRequestURL() + "' não encontrado para método '" + e.getHttpMethod() + "'";

        Error response = new Error(
                MessagesEnum.HTTP_404_NOT_FOUND.getCode(),
                errorReason,
                e.getMessage(),
                MessagesEnum.HTTP_404_NOT_FOUND.getDescription()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Error> missingRequestHeaderException(MissingRequestHeaderException e) {
        log.error(e.getClass().getSimpleName(), e);

        var errorReason = "Request Header '" + e.getHeaderName() + "' não foi encontrado.";

        var response = new Error(
                MessagesEnum.HTTP_400_BAD_REQUEST.getCode(),
                errorReason,
                e.getMessage(),
                MessagesEnum.HTTP_400_BAD_REQUEST.getDescription()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getClass().getSimpleName(), e);

        var errorReason = "O Método '" + e.getMethod() + "' não é permitido aqui.";

        Error response = new Error(
                GenericErrorsEnum.METHOD_NOT_ALLOWED.getCode(),
                errorReason,
                e.getMessage(),
                GenericErrorsEnum.METHOD_NOT_ALLOWED.getDescription()
        );

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getClass().getSimpleName(), e);

        Error response = new Error(
                GenericErrorsEnum.BAD_REQUEST.getCode(),
                GenericErrorsEnum.BAD_REQUEST.getReason(),
                (e.getBindingResult().getFieldError() != null)
                        ? e.getBindingResult().getFieldError().getDefaultMessage()
                        : e.getMessage(),
                GenericErrorsEnum.BAD_REQUEST.getDescription()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    public ResponseEntity<Error> standardException(Exception e) {
        log.error(e.getClass().getSimpleName(), e);
        Error response = new Error(
                GenericErrorsEnum.ERROR_GENERIC.getCode(),
                GenericErrorsEnum.ERROR_GENERIC.getReason(),
                e.getMessage(),
                GenericErrorsEnum.ERROR_GENERIC.getDescription()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = {DataIntegrityViolationException.class,
            ConstraintViolationException.class, SQLException.class, TransactionSystemException.class, JpaSystemException.class})
    public ResponseEntity<Object> handleExceptionDataIntegrate(Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex);
        Error response = new Error(
                GenericErrorsEnum.BAD_REQUEST.getCode(),
                GenericErrorsEnum.BAD_REQUEST.getReason(),
                ex.getMessage(),
                GenericErrorsEnum.BAD_REQUEST.getDescription()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> NotFoundException(Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex);
        Error response = new Error(
                GenericErrorsEnum.NOT_FOUND.getCode(),
                GenericErrorsEnum.NOT_FOUND.getReason(),
                ex.getMessage(),
                GenericErrorsEnum.NOT_FOUND.getDescription()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
