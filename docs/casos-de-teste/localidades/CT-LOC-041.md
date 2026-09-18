# Caso de Teste - Enviar JSON malformado na inclusão - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-041 |
| **Nome do Caso de Teste** | Enviar JSON malformado na inclusão |
| **Funcionalidade Testada** | Cadastro de Localidade / Desserialização |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Robustez / Sintaxe |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a requisição de cadastro de localidade enviada com sintaxe JSON malformada (corrompida ou incompleta) é interceptada e rejeitada com status HTTP 400 Bad Request no formato RFC 7807, sem expor stack trace. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Endpoint `POST /api/v1/localidades` disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `POST /api/v1/localidades` contendo payload com JSON sintaticamente corrompido (ex.: `{"nome": "Promotoria de Justiça", "cep": }`). |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar o corpo do erro no formato ProblemDetail (RFC 7807) e conferir ausência de vazamento de stack trace ou dados internos da JVM/framework. |
| 4 | Consultar o banco de dados para certificar que nenhum registro ou fragmento de dados foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `""` (Não gerado) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Corpo da Requisição | `{"nome": "Promotoria de Justiça", "cidadeId": "c1d00001-0000-0000-0000-000000000001", "cep": }` |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `POST` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição rejeitada pelo parser de desserialização retornando status HTTP `400 Bad Request`. |
| 2 | Resposta estruturada conforme RFC 7807 (ProblemDetail) informando erro de sintaxe JSON. |
| 3 | Nenhum stack trace de conversão ou detalhe interno de implementação é vazado ao cliente. |
| 4 | Nenhuma gravação parcial ou total ocorre no banco de dados. |

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
| Observações | Proteção contra falhas não tratadas de parsing e desserialização JSON conforme HU-10.1. |
