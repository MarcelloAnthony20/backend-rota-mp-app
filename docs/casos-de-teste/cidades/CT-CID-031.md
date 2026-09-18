# Caso de Teste - Consultar cidades sem perfil autorizado ou sem token - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-031 |
| **Nome do Caso de Teste** | Consultar cidades sem perfil autorizado ou sem token |
| **Funcionalidade Testada** | Segurança / Autenticação na Consulta |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Autenticação / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a consulta de cidades sem fornecimento de token de autenticação (ou com token ausente no cabeçalho) é bloqueada com status HTTP 401 Unauthorized, protegendo os dados cadastrais da aplicação. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Endpoint de listagem de cidades operacional (`GET /api/v1/cidades`). |
| 2 | Filtro de segurança Spring Security ativo e exigindo autenticação para o endpoint. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `GET /api/v1/cidades` sem informar o cabeçalho `Authorization`. |
| 2 | Consultar o código de status HTTP retornado pelo servidor. |
| 3 | Validar a mensagem de erro retornada pelo filtro de autenticação. |
| 4 | Confirmar que nenhum dado cadastral de cidades foi exposto na resposta. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | `""` (Não aplicável / consulta geral) |
| Nome da Cidade | `""` (Não aplicável) |
| Código da Cidade | `""` (Não aplicável) |
| Status / Ativo | `""` (Não aplicável) |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `""` (Ausente / não informado) |
| Perfil do Usuário | `Não autenticado (Anônimo)` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição bloqueada pelo gateway/filtro retornando status HTTP `401 Unauthorized`. |
| 2 | Resposta estruturada conforme RFC 7807 indicando necessidade de autenticação prévia. |
| 3 | Nenhuma informação sobre cidades cadastradas é retornada no corpo da resposta. |

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
| Observações | Proteção de sigilo e integridade de dados contra acessos não autenticados aos serviços do Rota MP APP conforme HU-08.2. |

