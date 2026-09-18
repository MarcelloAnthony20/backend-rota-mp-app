# Caso de Teste - Inativar cidade com DELETE válido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-022 |
| **Nome do Caso de Teste** | Inativar cidade com DELETE válido |
| **Funcionalidade Testada** | Inativação de cidade |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste Funcional / Inativação Lógica / Idempotência |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a inativação lógica da Seed Cidade B através de requisição DELETE válida realizada por usuário com perfil de Gestor, confirmando o retorno com status HTTP 204 No Content sem corpo de resposta, a atualização de status no banco de dados e o comportamento idempotente na repetição da chamada. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade B previamente cadastrada e com status ativo na base de dados (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`, `ativo = true`). |
| 3 | Endpoint de inativação de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição com método `DELETE` para o endpoint da Seed Cidade B (`/api/v1/cidades/c1d00002-0000-0000-0000-000000000002`) com cabeçalho de autorização de Gestor. |
| 2 | Consultar o código de status HTTP retornado e confirmar a ausência de corpo na resposta. |
| 3 | Verificar no banco de dados se a Seed Cidade B teve seu status atualizado para inativo (`ativo = false`). |
| 4 | Repetir a mesma requisição `DELETE` para o mesmo identificador (`c1d00002-0000-0000-0000-000000000002`). |
| 5 | Conferir o código de status HTTP retornado na segunda execução para validação de idempotência. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade B |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Inativo (false)` |
| Método HTTP | `DELETE` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | A primeira operação retorna status HTTP `204 No Content` sem corpo de resposta. |
| 2 | O registro da Seed Cidade B (`Dourados`) tem seu status alterado para inativo no banco de dados (`ativo = false`) por exclusão lógica. |
| 3 | A repetição da requisição `DELETE` retorna igualmente status HTTP `204 No Content` mantendo o registro inativo (garantia de idempotência). |
| 4 | A exclusão física do registro não ocorre, preservando a integridade histórica de relacionamentos. |

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
| Observações | Ponto de mutação da linhagem contínua: a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`) passa ao estado inativo (`ativo = false`), preparando o cenário para os testes de integridade referencial CT-CID-036 e CT-LOC-045. Diretriz mandatória: deleção/inativação sempre retorna status HTTP 204 No Content conforme HU-08.4. |
