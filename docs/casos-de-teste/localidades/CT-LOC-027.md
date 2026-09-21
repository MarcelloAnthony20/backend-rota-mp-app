# Caso de Teste - Alterar número da localidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-027 |
| **Nome do Caso de Teste** | Alterar número da localidade |
| **Funcionalidade Testada** | Alteração de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Atualização de Endereço |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a alteração do número da localidade para o valor válido '250' dentro do limite de 20 caracteres, mantendo os demais atributos inalterados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`d1000001-0000-0000-0000-000000000001`) vinculada à Cidade A (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Serviço de alteração de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de localidade (`PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo atualizando o campo Número para `250` e mantendo os demais campos com seus valores inalterados. |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e a resposta. |
| 5 | Verificar na base de dados se o número foi atualizado no endereço da localidade. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (mantido) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - mantido) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - mantido) |
| CEP | `79002-000` (mantido) |
| Logradouro | `Rua da Paz` (mantido) |
| Número | `250` (Atualizado) |
| Bairro | `Jardim dos Estados` (mantido) |
| Complemento | `Edifício Sede` (mantido) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `true` (mantido) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | O campo Número do endereço da localidade é atualizado para `250`. |
| 3 | Todos os demais campos mantêm seus valores originais íntegros. |
| 4 | Resposta estruturada retornada reflete a numeração atualizada. |

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
| Observações | Atualização cadastral de número predial conforme HU-10.3 (limite DER: 20 caracteres). |
