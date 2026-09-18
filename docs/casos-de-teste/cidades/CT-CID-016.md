# Caso de Teste - Alterar nome da Cidade A com valor válido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-016 |
| **Nome do Caso de Teste** | Alterar nome da Cidade A com valor válido |
| **Funcionalidade Testada** | Alteração de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Atualização Cadastral |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a atualização cadastral do nome da Seed Cidade A para "Campo Grande Alterada", mantendo intactos o código 5002704 e o status ativo, com retorno HTTP 200 OK e confirmação de que o payload `AtualizarCidadeRequest` contém apenas `nome`, `codigo` e `ativo` (sem ID no corpo). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Seed Cidade A cadastrada previamente no banco de dados com ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"` e status ativo (`true`). |
| 3 | Serviço de atualização de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição de alteração para o endpoint da Seed Cidade A (`PUT /api/v1/cidades/c1d00001-0000-0000-0000-000000000001`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON (`AtualizarCidadeRequest`) contendo apenas os campos atualizados: `{"nome": "Campo Grande Alterada", "codigo": "5002704", "ativo": true}` (o ID é fornecido exclusivamente na URL). |
| 4 | Consultar o código de status HTTP retornado e a estrutura da resposta. |
| 5 | Consultar a base de dados para confirmar que o nome da Seed Cidade A foi atualizado e os demais campos preservados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade (Path Variable) | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande Alterada` |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`AtualizarCidadeRequest`) | `{"nome": "Campo Grande Alterada", "codigo": "5002704", "ativo": true}` (sem ID no corpo) |
| Método HTTP | `PUT` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | Nome da Seed Cidade A atualizado no banco de dados para `"Campo Grande Alterada"`. |
| 3 | Código `5002704` e status ativo (`ativo = true`) permanecem preservados e inalterados. |
| 4 | Resposta estruturada conforme DTO `CidadeResponse` com o novo nome e dados consolidados. |

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
| Observações | Mutação canônica da Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`) no pipeline de testes: novo estado consolidado (`Campo Grande Alterada`, ativo) será referenciado pelas localidades conforme HU-08.3. |
