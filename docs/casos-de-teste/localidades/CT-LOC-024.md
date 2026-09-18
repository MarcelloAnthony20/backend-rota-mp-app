# Caso de Teste - Alterar tipo da localidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-024 |
| **Nome do Caso de Teste** | Alterar tipo da localidade |
| **Funcionalidade Testada** | Alteração de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Atualização de Tipo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a alteração do tipo da localidade para outro tipo ativo e cadastrado no sistema, mantendo os demais 10 atributos inalterados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`l0c00001-0000-0000-0000-000000000001`) vinculada ao Tipo `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça). |
| 3 | Novo Tipo de localidade ativo cadastrado (`t1p00002-0000-0000-0000-000000000002` - Sede Administrativa). |
| 4 | Serviço de alteração de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de localidade (`PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo atualizando o campo Tipo de Localidade (FK) para o novo tipo ativo (`t1p00002-0000-0000-0000-000000000002`) e mantendo os outros 10 campos intactos. |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e a resposta estruturada. |
| 5 | Conferir na base de dados se o novo vínculo de tipo foi atualizado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (mantido) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - mantido) |
| Tipo de Localidade (FK) | `t1p00002-0000-0000-0000-000000000002` (Sede Administrativa - Atualizado) |
| CEP | `79002-000` (mantido) |
| Logradouro | `Rua da Paz` (mantido) |
| Número | `140` (mantido) |
| Bairro | `Jardim dos Estados` (mantido) |
| Complemento | `Edifício Sede` (mantido) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `true` (mantido) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | O campo Tipo de Localidade (FK) passa a referenciar `t1p00002-0000-0000-0000-000000000002`. |
| 3 | Todos os outros 10 atributos permanecem inalterados e consistentes na base de dados. |
| 4 | A resposta reflete com fidelidade a nova classificação da entidade. |

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
| Observações | Atualização de classificação e tipo conforme HU-10.3 e HU-10.5. |
