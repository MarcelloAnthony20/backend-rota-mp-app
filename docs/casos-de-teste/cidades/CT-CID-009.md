# Caso de Teste - Ativar cidade já ativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-009 |
| **Nome do Caso de Teste** | Ativar cidade já ativa |
| **Funcionalidade Testada** | Alteração de status da cidade / Ativação |
| **Prioridade** | Média |
| **Tipo de Teste** | Idempotência / Teste de Estado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a solicitação de ativação de uma cidade que já se encontra com status ativo (Seed Cidade A) é processada com idempotência, retornando status HTTP 200 OK via método PATCH na rota de ativação. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Seed Cidade A (ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"`) previamente cadastrada no banco de dados. |
| 3 | A Seed Cidade A já se encontra com status ativo (`ativo = true`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição PATCH para a rota de ativação da Seed Cidade A (`PATCH /api/v1/cidades/c1d00001-0000-0000-0000-000000000001/ativar`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Consultar o código de status HTTP retornado na resposta da API. |
| 4 | Consultar o banco de dados e confirmar que o registro permanece ativo (`ativo = true`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande` |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Método HTTP | `PATCH` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação processada com sucesso retornando status HTTP `200 OK`. |
| 2 | O registro da Seed Cidade A permanece com status ativo (`ativo = true`) sem duplicidades ou efeitos colaterais. |
| 3 | O payload da resposta (`CidadeResponse`) confirma o ID `c1d00001-0000-0000-0000-000000000001`, nome `"Campo Grande"`, código `"5002704"` e status `ativo = true`. |
| 4 | Nenhuma alteração indevida ocorre nos campos de auditoria ou dados cadastrais. |

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
| Observações | Atendimento ao critério de idempotência de HU-08.4: processamento idempotente da ativação de uma cidade que já se encontre ativa. Utiliza explicitamente a Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`) e o método RESTful PATCH conforme implementado no backend. |
