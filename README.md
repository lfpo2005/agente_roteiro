# Documentação da API Agente Roteiro

## Visão Geral

Este documento descreve os endpoints disponíveis na API do sistema Agente Roteiro. A API fornece funcionalidades para autenticação de usuários, gerenciamento de conteúdos e geração de roteiros.

## Autenticação

### Base URL: `/public/auth`

| Método | Endpoint | Descrição | Corpo da Requisição | Resposta | Código |
|--------|----------|-----------|---------------------|----------|--------|
| POST | `/public/auth/signup` | Cria um novo usuário | `UserRequestDto` (username, email, password, fullName) | Mensagem de sucesso | 201 |
| POST | `/public/auth/login` | Autentica um usuário | `LoginDto` (username, password) | `JwtDto` (token JWT) | 200 |

## Gerenciamento de Usuários

### Base URL: `/users`

| Método | Endpoint | Descrição | Parâmetros | Resposta | Autenticação |
|--------|----------|-----------|------------|----------|--------------|
| GET | `/users` | Lista todos os usuários | Paginação (page, size, sort) | Page de `UserResponseDto` | ADMIN |
| GET | `/users/{userId}` | Obtém um usuário pelo ID | UUID do usuário | `UserResponseDto` | USER (apenas próprio usuário) |
| GET | `/users/find_email` | Busca usuário por email | email (query param) | `UserResponseDto` | USER |

## Gerenciamento de Conteúdos do Usuário

### Base URL: `/my-content`

| Método | Endpoint | Descrição | Parâmetros | Resposta | Autenticação |
|--------|----------|-----------|------------|----------|--------------|
| GET | `/my-content` | Lista resumos dos conteúdos do usuário | Paginação (page, size, sort) | Page de `ContentSummaryDto` | USER |
| GET | `/my-content/{contentId}` | Obtém detalhes de um conteúdo específico | UUID do conteúdo | `ContentGeneration` | USER |
| GET | `/my-content/{contentId}/download` | Baixa pacote de conteúdo (textos e áudio) | UUID do conteúdo | Resource (arquivo) | USER |
| DELETE | `/my-content/{contentId}` | Exclui um conteúdo | UUID do conteúdo | - | USER |
| GET | `/my-content/count` | Conta o total de conteúdos do usuário | - | Long | USER |

## Geração de Conteúdo

### Base URL: `/agent`

| Método | Endpoint | Descrição | Corpo da Requisição | Resposta | Autenticação |
|--------|----------|-----------|---------------------|----------|--------------|
| POST | `/agent/generate` | Inicia a geração de conteúdo | `ContentGenerationRequest` | `GenerationResponseDto` | USER |

## Conteúdo de Orações

### Base URL: `/prayer`

| Método | Endpoint | Descrição | Parâmetros | Resposta | Autenticação |
|--------|----------|-----------|------------|----------|--------------|
| GET | `/prayer/content` | Obtém conteúdo de orações | - | Dados de orações | USER |
| GET | `/prayer/options` | Obtém opções de orações | - | Opções disponíveis | USER |

## Conteúdo Estoico

### Base URL: `/stoic`

| Método | Endpoint | Descrição | Parâmetros | Resposta | Autenticação |
|--------|----------|-----------|------------|----------|--------------|
| GET | `/stoic/content` | Obtém conteúdo estoico | - | Dados estoicos | USER |

## Durações

### Base URL: `/durations`

| Método | Endpoint | Descrição | Parâmetros | Resposta | Autenticação |
|--------|----------|-----------|------------|----------|--------------|
| GET | `/durations` | Obtém opções de duração | - | Lista de durações disponíveis | USER |

## Conteúdo do YouTube

### Base URL: `/youtube`

| Método | Endpoint | Descrição | Parâmetros | Resposta | Autenticação |
|--------|----------|-----------|------------|----------|--------------|
| GET | `/youtube/content` | Obtém conteúdo do YouTube | - | Dados do YouTube | USER |

## Autenticação

Exceto para os endpoints em `/public/`, todas as requisições devem incluir um token JWT válido no cabeçalho `Authorization` no formato:

```
Authorization: Bearer {seu_token_jwt}
```

## Níveis de Acesso

- **USER**: Usuário autenticado com acesso às suas próprias informações e conteúdos
- **ADMIN**: Usuário administrador com acesso a informações de todos os usuários

## Respostas de Erro

| Código | Descrição |
|--------|-----------|
| 400 | Requisição inválida |
| 401 | Não autorizado |
| 403 | Acesso proibido |
| 404 | Recurso não encontrado |
| 500 | Erro interno do servidor | 