# Caso de Teste - Alterar complemento da localidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-026 |
| **Nome do Caso de Teste** | Alterar complemento da localidade |
| **Funcionalidade Testada** | Alteração de Localidade |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Positivo / Campo Opcional |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a alteração do complemento opcional da localidade para o valor 'Anexo III' dentro do limite de 200 caracteres, mantendo os demais 10 atributos íntegros. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`l0c00001-0000-0000-0000-000000000001`) vinculada à Cidade A (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Serviço de alteração de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de localidade (`PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo atualizando o campo Complemento para `Anexo III` e mantendo os outros 10 campos inalterados. |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e a resposta. |
| 5 | Conferir no banco de dados se o novo complemento foi gravado. |

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
| Complemento | `Anexo III` (Atualizado) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `true` (mantido) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | O campo Complemento da localidade é atualizado para `Anexo III`. |
| 3 | Todos os outros 10 atributos permanecem inalterados e consistentes na base de dados. |
| 4 | O payload de resposta reflete o complemento atualizado conforme contrato. |

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
| Observações | Atualização de campo opcional de endereço conforme HU-10.3 (limite DER: 200 caracteres). |
