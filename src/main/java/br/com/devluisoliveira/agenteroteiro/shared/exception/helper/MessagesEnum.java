package br.com.devluisoliveira.agenteroteiro.shared.exception.helper;

import lombok.Getter;

@Getter
public enum MessagesEnum {

    EXCEPTION_ARCHIVE_MESSAGES(GenericErrorsEnum.ERROR_GENERIC.getCode(), GenericErrorsEnum.ERROR_GENERIC.getDescription(), GenericErrorsEnum.ERROR_GENERIC.getReason(), "Exception Resources."),
    UNEXPECTED_ERROR_SEARCH(GenericErrorsEnum.ERROR_GENERIC.getCode(), GenericErrorsEnum.ERROR_GENERIC.getDescription(), GenericErrorsEnum.ERROR_GENERIC.getReason(), "Ocorreu um erro inesperado durante chamada do serviço."),
    ERROR_TOKEN_NOT_VALIDATE(GenericErrorsEnum.UNAUTHORIZED.getCode(), GenericErrorsEnum.UNAUTHORIZED.getDescription(), GenericErrorsEnum.UNAUTHORIZED.getReason(), "Ocorreu um erro inesperado ao validar o token de segurança."),
    ERROR_TOKEN_IS_EMPTY(GenericErrorsEnum.FORBIDDEN.getCode(), GenericErrorsEnum.FORBIDDEN.getDescription(), GenericErrorsEnum.FORBIDDEN.getReason(), "O token de segurança (obrigatório) não foi enviado."),
    HTTP_400_BAD_REQUEST(GenericErrorsEnum.BAD_REQUEST.getCode(), GenericErrorsEnum.BAD_REQUEST.getDescription(), GenericErrorsEnum.BAD_REQUEST.getReason(), "Requisição do cliente não pôde ser entendida pelo servidor."),
    HTTP_401_UNAUTHORIZED(GenericErrorsEnum.UNAUTHORIZED.getCode(), GenericErrorsEnum.UNAUTHORIZED.getDescription(), GenericErrorsEnum.UNAUTHORIZED.getReason(), "Autenticação é requerida e falhou ou não foi devidamente fornecida."),
    HTTP_403_FORBIDDEN(GenericErrorsEnum.FORBIDDEN.getCode(), GenericErrorsEnum.FORBIDDEN.getDescription(), GenericErrorsEnum.FORBIDDEN.getReason(), "Requisição válida mas o servidor recusa a ação. Usuário pode não ter as permissões necessárias."),
    HTTP_404_NOT_FOUND(GenericErrorsEnum.NOT_FOUND.getCode(), GenericErrorsEnum.NOT_FOUND.getDescription(), GenericErrorsEnum.NOT_FOUND.getReason(), "Recurso não encontrado."),
    HTTP_412_PRECONDITION_FAILED(GenericErrorsEnum.PRECONDITION_FAILED.getCode(), GenericErrorsEnum.PRECONDITION_FAILED.getDescription(), GenericErrorsEnum.PRECONDITION_FAILED.getReason(), "Tempo esgotado no servidor esperando pela requisição."),
    HTTP_503_UNAVAILABLE(GenericErrorsEnum.UNAVAILABLE.getCode(), GenericErrorsEnum.UNAVAILABLE.getDescription(), GenericErrorsEnum.UNAVAILABLE.getReason(), "Serviço indisponível"),
    HTTP_504_TIMEOUT_OAUTH(GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getCode(), GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getDescription(), GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getReason(), "Tempo esgotado no servidor esperando pela requisição (timeout no serviço da Oauth)."),
    HTTP_504_TIMEOUT_RISK_MANAGEMENT(GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getCode(), GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getDescription(), GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getReason(), "Tempo esgotado no servidor esperando pela requisição (timeout no serviço da HPFMS)."),
    HTTP_504_TIMEOUT(GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getCode(), GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getDescription(), GenericErrorsEnum.ERROR_REQUEST_TIMEOUT.getReason(), "Tempo esgotado no servidor esperando pela requisição."),
    HTTP_500_ERROR_GENERIC(GenericErrorsEnum.ERROR_GENERIC.getCode(), GenericErrorsEnum.ERROR_GENERIC.getDescription(), GenericErrorsEnum.ERROR_GENERIC.getReason(), "Erro genérico retornado no caso de uma exceção não mapeada."),
    JSON_CONVERTER_ERROR(GenericErrorsEnum.ERROR_GENERIC.getCode(), GenericErrorsEnum.ERROR_GENERIC.getDescription(), GenericErrorsEnum.ERROR_GENERIC.getReason(), "Erro ao tentar fazer o parte de string para json."),
    TOKEN_DECODE_ERROR(GenericErrorsEnum.ERROR_GENERIC.getCode(), GenericErrorsEnum.ERROR_GENERIC.getDescription(), GenericErrorsEnum.ERROR_GENERIC.getReason(), "Erro ao tentar fazer o decode do toke."),

    DATA_MAPPING_ERROR(GenericErrorsEnum.ERROR_GENERIC.getCode(), GenericErrorsEnum.ERROR_GENERIC.getDescription(), GenericErrorsEnum.ERROR_GENERIC.getReason(), "Erro ao converter valores de entrada/saida do serviço");

    private String code;
    private String description;
    private String name;
    private String value;

    private MessagesEnum(String code, String description, String value, String name) {
        this.description = description;
        this.code = code;
        this.value = value;
        this.name = name;
    }
}
