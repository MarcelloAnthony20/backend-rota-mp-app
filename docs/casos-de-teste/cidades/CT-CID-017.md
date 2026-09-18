# Caso de Teste - Alterar código da cidade com valor válido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-017 |
| **Nome do Caso de Teste** | Alterar código da cidade com valor válido |
| **Funcionalidade Testada** | Alteração de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Atualização de Código |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a atualização do código da Seed Cidade A para um novo valor válido (`5002705`), mantendo íntegros o nome e o status ativo com retorno HTTP 200 OK e confirmação de que o payload `AtualizarCidadeRequest` não recebe ID no corpo. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Seed Cidade A (ID `c1d00001-0000-0000-0000-000000000001`) cadastrada previamente com status ativo na base de dados. |
| 3 | Novo código `5002705` não está em uso por nenhuma outra cidade na base de dados (distinto de todas as seeds). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição de alteração para o endpoint da Seed Cidade A (`PUT /api/v1/cidades/c1d00001-0000-0000-0000-000000000001`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON (`AtualizarCidadeRequest`) contendo os dados atualizados: `{"nome": "Campo Grande", "codigo": "5002705", "ativo": true}` (sem ID no corpo). |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 5 | Consultar a base de dados para confirmar a persistência do novo código `5002705` para a Seed Cidade A. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade (Path Variable) | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande` |
| Código da Cidade | `5002705` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`AtualizarCidadeRequest`) | `{"nome": "Campo Grande", "codigo": "5002705", "ativo": true}` (sem ID no corpo) |
| Método HTTP | `PUT` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | Código da Seed Cidade A atualizado na base de dados para `5002705`. |
| 3 | Nome (`"Campo Grande"`) e status (`ativo = true`) preservados sem alterações indevidas. |
| 4 | Resposta estruturada conforme DTO `CidadeResponse` com os dados devidamente atualizados. |

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
| Observações | Atualização de código com integridade e unicidade para a Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`) conforme HU-08.3. O payload `AtualizarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. |
