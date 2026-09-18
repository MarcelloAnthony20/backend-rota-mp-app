# Caso de Teste - Inativar cidade com identificador inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-035 |
| **Nome do Caso de Teste** | Inativar cidade com identificador inexistente |
| **Funcionalidade Testada** | Inativação de cidade / Recurso não encontrado |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Recurso Não Encontrado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de inativar uma cidade utilizando um identificador inexistente na base de dados (`99999999-9999-9999-9999-999999999999`) é rejeitada com retorno de status HTTP 404 Not Found estruturado conforme RFC 7807 (ProblemDetail). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | O identificador `99999999-9999-9999-9999-999999999999` comprovadamente não existe no cadastro de cidades. |
| 3 | Endpoint de inativação de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `DELETE /api/v1/cidades/99999999-9999-9999-9999-999999999999` com token de autorização de Gestor. |
| 2 | Consultar o código de status HTTP retornado pelo servidor. |
| 3 | Validar a estrutura do corpo da resposta de erro conforme o padrão RFC 7807 (ProblemDetail). |
| 4 | Confirmar que nenhuma inconsistência ou alteração espúria ocorreu na base de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | `99999999-9999-9999-9999-999999999999` (Inexistente) |
| Nome da Cidade | `""` (Não aplicável na inativação) |
| Código da Cidade | `""` (Não aplicável na inativação) |
| Status / Ativo | `""` (Não aplicável na inativação) |
| Método HTTP | `DELETE` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `404 Not Found`. |
| 2 | Corpo da resposta estruturado conforme RFC 7807 (ProblemDetail) informando que a cidade informada para inativação não foi encontrada. |
| 3 | Nenhuma modificação é gerada no banco de dados. |
| 4 | O sistema opera com estabilidade sem gerar erros internos (HTTP 500). |

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
| Observações | Atendimento à diretriz mandatória de retorno HTTP 404 Not Found (ProblemDetail RFC 7807) para recurso inexistente conforme HU-08.4. Utiliza o UUID padrão de recurso inexistente `99999999-9999-9999-9999-999999999999`. |
