# Caso de Teste - Inativar cidade já inativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-008 |
| **Nome do Caso de Teste** | Inativar cidade já inativa |
| **Funcionalidade Testada** | Inativação de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Idempotência / Teste de Estado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a requisição de inativação da Seed Cidade B quando esta já se encontra inativa é processada de forma idempotente, retornando status HTTP 204 No Content sem corpo de resposta. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de inativação de cidades disponível e operacional. |
| 3 | A Seed Cidade B (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`) já se encontra com status inativo (`ativo = false`) na base de dados (previamente inativada no pipeline). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição DELETE para inativação da Seed Cidade B (`DELETE /api/v1/cidades/c1d00002-0000-0000-0000-000000000002`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Consultar o código de status HTTP retornado e certificar a ausência de corpo na resposta. |
| 4 | Consultar a base de dados para confirmar a manutenção do status inativo (`ativo = false`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade B |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Inativo (false)` |
| Método HTTP | `DELETE` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação processada com sucesso retornando status HTTP `204 No Content`. |
| 2 | Resposta sem corpo de dados (Empty Body), em conformidade com a especificação HTTP 204. |
| 3 | O registro da Seed Cidade B permanece com status inativo (`ativo = false`) no banco de dados, garantindo idempotência. |
| 4 | Nenhuma alteração colateral, duplicidade ou erro de servidor (500) é gerado. |

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
| Observações | Alinhado com a diretriz mandatória: deleção ou inativação sempre retorna HTTP 204 No Content e critério de idempotência de HU-08.4. Utiliza explicitamente a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`). |
