package br.com.devluisoliveira.agenteroteiro.shared.exception.helper;

import lombok.Getter;

@Getter
public enum GenericErrorsEnum {
    BAD_REQUEST("400", "INVALID_ARGUMENT", "Client specified an invalid argument, request body, query param or filed is invalid"),
    UNAUTHORIZED("401", "UNAUTHORIZED", "O cliente deve se autenticar para obter a resposta solicitada."),
    FORBIDDEN("403", "PERMISSION_DENIED", "Authenticated user has no permission to access the requested resource"),
    NOT_FOUND("404", "NOT_FOUND", "A specified resource is not found"),
    METHOD_NOT_ALLOWED("405", "METHOD_NOT_ALLOWED", "O método de solicitação é conhecido pelo servidor, mas foi desativado e não pode ser usado."),
    NOT_ACCEPTABLE("406", "NOT_ACCEPTABLE", "Não foi encontrado nenhum conteúdo seguindo os critérios fornecidos pelo agente do usuário."),
    PROXY_AUTHENTICATION_REQUIRED("407", "PROXY_AUTHENTICATION_REQUIRED", "É necessário que a autenticação seja feita por um proxy."),
    REQUEST_TIMEOUT("408", "REQUEST_TIMEOUT", "o servidor derrubou esta conexão."),
    CONFLICT("409", "CONFLICT", "A requisição conflita com o estado atual do servidor."),
    GONE("410", "GONE", "O conteúdo requisitado foi permanentemente deletado do servidor."),
    LENGTH_REQUIRED("411", "LENGTH_REQUIRED", "O campo Content-Length do cabeçalho não está definido."),
    PRECONDITION_FAILED("412", "PRECONDITION_FAILED", "O cliente indicou nos seus cabeçalhos pré-condições que o servidor não atende."),
    PAYLOAD_TOO_LARGE("413", "PAYLOAD_TOO_LARGE", "A entidade requisição é maior do que os limites definidos pelo servidor."),
    URI_TOO_LONG("414", "URI_TOO_LONG", "A URI requisitada pelo cliente é maior do que o servidor aceita para interpretar."),
    UNSUPPORTED_MEDIA_TYPE("415", "UNSUPPORTED_MEDIA_TYPE", "O formato de mídia dos dados requisitados não é suportado pelo servidor."),
    REQUESTED_RANGE_NOT_SATISFIABLE("416", "REQUESTED_RANGE_NOT_SATISFIABLE", "O trecho especificado pelo campo Range do cabeçalho na requisição não pode ser preenchido."),
    EXPECTATION_FAILED("417", "EXPECTATION_FAILED", "A expectativa indicada pelo campo Expect do cabeçalho da requisição não pode ser satisfeita pelo servidor."),
    I_AM_A_TEAPOT("418", "I_AM_A_TEAPOT", "O servidor recusa a tentativa."),
    UNPROCESSABLE_ENTITY("422", "UNPROCESSABLE_ENTITY", "A requisição está inabilitada para ser seguida devido a erros semânticos."),
    LOCKED("423", "LOCKED", "O acesso ao recurso acessado está travado."),
    FAILED_DEPENDENCY("424", "FAILED_DEPENDENCY", "A requisição falhou devido a falha em requisição prévia."),
    TOO_EARLY("425", "TOO_EARLY", "o servidor não irá processar uma requisição que pode ser refeita."),
    UPGRADE_REQUIRED("426", "UPGRADE_REQUIRED", "O servidor se recusa a executar a requisição usando o protocolo corrente."),
    PRECONDITION_REQUIRED("428", "PRECONDITION_REQUIRED", "O servidor de origem requer que a resposta seja condicional."),
    TOO_MANY_REQUESTS("429", "TOO_MANY_REQUESTS", "O usuário enviou muitas requisições num dado tempo (limitação de frequência)."),
    REQUEST_HEADER_FIELDS_TOO_LARGE("431", "REQUEST_HEADER_FIELDS_TOO_LARGE", "O servidor não quer processar a requisição. Os campos de cabeçalho são muito grandes."),
    UNAVAILABLE_FOR_LEGAL_REASONS("451", "UNAVAILABLE_FOR_LEGAL_REASONS", "O usuário requisitou um recurso ilegal."),
    UNAVAILABLE("503", "UNAVAILABLE", "Service unavailable"),
    ERROR_REQUEST_TIMEOUT("504", "TIMEOUT", "Request timeout exceeded. Try it later"),
    ERROR_GENERIC("500", "INTERNAL_SERVER_ERROR", "Server error");

    private String code;
    private String description;
    private String  	reason;

    private GenericErrorsEnum(String code, String description, String reason) {
        this.code = code;
        this.description = description;
        this.reason = reason;
    }
}
