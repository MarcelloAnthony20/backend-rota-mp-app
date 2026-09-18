# Caso de Teste - Enviar payload vazio na inclusão - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-040 |
| **Nome do Caso de Teste** | Enviar payload vazio na inclusão |
| **Funcionalidade Testada** | Cadastro de Localidade / Validação de Entrada |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Robustez |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de criar localidade enviando uma requisição POST sem corpo (payload vazio / Content-Length 0) é rejeitada com status HTTP 400 Bad Request no formato RFC 7807, sem realizar persistência. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Endpoint `POST /api/v1/localidades` disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `POST /api/v1/localidades` com corpo vazio (`Content-Length: 0` ou sem corpo) acompanhada do token do gestor. |
| 2 | Consultar o código de status HTTP retornado na resposta. |
| 3 | Validar a mensagem de erro retornada no formato RFC 7807 (ProblemDetail). |
| 4 | Consultar o banco de dados e verificar que nenhum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `""` (Vazio) |
| Nome da Localidade | `""` (Vazio) |
| Cidade (FK) | `""` (Vazio) |
| Tipo de Localidade (FK) | `""` (Vazio) |
| CEP | `""` (Vazio) |
| Logradouro | `""` (Vazio) |
| Número | `""` (Vazio) |
| Bairro | `""` (Vazio) |
| Complemento | `""` (Vazio) |
| Local Sigiloso | `""` (Vazio) |
| Status / Ativo | `""` (Vazio) |
| Corpo da Requisição | `""` (Payload Vazio) |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `POST` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição rejeitada pelo sistema retornando status HTTP `400 Bad Request`. |
| 2 | Resposta estruturada em conformidade com o formato ProblemDetail (RFC 7807). |
| 3 | Mensagem de erro informativa indicando que o corpo da requisição é obrigatório e não pode ser nulo ou vazio. |
| 4 | Nenhum registro é persistido na base de dados (atomicidade garantida). |

## Resultado Obtido

| Campo | Valor |
|---------|---------|
| Resultado Obtido | A preencher durante a execução |

## Status

| Opções |
|---------|
| Aprovado |
| Reprovado |
| Bloqueado |
| Não Executado |

## Observações

| Campo | Descrição |
|---------|---------|
| Observações | Tratamento seguro de corpo de requisição ausente e validação de entrada conforme HU-10.1. |
