# Caso de Teste - Alterar status da localidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-030 |
| **Nome do Caso de Teste** | Alterar status da localidade |
| **Funcionalidade Testada** | Transição de Estado de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Transição de Estado / Contrato |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a alteração explícita do status da localidade de ativo para inativo (`ativo = false`) através do endpoint de atualização PUT, persistindo o novo estado no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`l0c00001-0000-0000-0000-000000000001`) vinculada à Cidade A (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada) com status ativo (`ativo = true`). |
| 3 | Serviço de atualização de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de atualização de localidade (`PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo com a flag Status / Ativo alterada para `false`, mantendo os demais 10 campos íntegros. |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 5 | Conferir na base de dados se o status da localidade foi atualizado para inativo (`ativo = false`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (mantido) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - mantido) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - mantido) |
| CEP | `79002-000` (mantido) |
| Logradouro | `Rua da Paz` (mantido) |
| Número | `140` (mantido) |
| Bairro | `Jardim dos Estados` (mantido) |
| Complemento | `Edifício Sede` (mantido) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `false` (alteração de `true` para `false`) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK` (via PUT de atualização cadastral). |
| 2 | O campo `ativo` da localidade é gravado com valor `false` no banco de dados. |
| 3 | Todos os outros 10 campos da entidade e seu respectivo endereço permanecem preservados e inalterados. |
| 4 | Nota mandatória: Quando a inativação/deleção lógica for executada através do endpoint dedicado de deleção (`DELETE /api/v1/localidades/{id}`), o retorno deve ser obrigatoriamente HTTP `204 No Content` sem corpo de resposta. |

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
| Observações | Alinhamento com a diretriz mandatória de ciclo de vida e transição de estado de localidade conforme HU-10.3 e HU-10.4. |
