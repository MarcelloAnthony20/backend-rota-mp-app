# Caso de Teste - Alterar cidade da localidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-023 |
| **Nome do Caso de Teste** | Alterar cidade da localidade |
| **Funcionalidade Testada** | Alteração de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Atualização de Relacionamento |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a alteração da cidade vinculada à localidade para outra cidade existente e ativa, mantendo a integridade de todos os demais atributos. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`l0c00001-0000-0000-0000-000000000001`) vinculada inicialmente à Cidade A (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Nova Cidade ativa cadastrada e disponível (`c1d00003-0000-0000-0000-000000000003` - Três Lagoas). |
| 4 | Serviço de alteração de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de localidade (`PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo atualizando o campo Cidade (FK) para o ID da nova cidade ativa (`c1d00003-0000-0000-0000-000000000003`) e mantendo os outros 10 campos inalterados. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a resposta estruturada. |
| 5 | Verificar na base de dados a persistência da nova chave estrangeira de cidade na localidade. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (mantido) |
| Cidade (FK) | `c1d00003-0000-0000-0000-000000000003` (Três Lagoas - Atualizado) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - mantido) |
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
| 2 | O atributo Cidade (FK) passa a referenciar o novo ID de cidade ativo (`c1d00003-0000-0000-0000-000000000003`). |
| 3 | Todos os demais 10 atributos da entidade e de seu endereço permanecem íntegros e inalterados. |
| 4 | A resposta JSON reflete com fidelidade o novo vínculo de cidade associado. |

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
| Observações | Atualização de relacionamento com integridade de chave estrangeira conforme HU-10.3. |
