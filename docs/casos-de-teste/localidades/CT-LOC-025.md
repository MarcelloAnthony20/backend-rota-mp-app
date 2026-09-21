# Caso de Teste - Alterar bairro da localidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-025 |
| **Nome do Caso de Teste** | Alterar bairro da localidade |
| **Funcionalidade Testada** | Alteração de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Atualização de Endereço |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a alteração do bairro da localidade para um valor válido ('Chácara Cachoeira') dentro do limite de 100 caracteres, mantendo todos os outros atributos íntegros. |

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
| 2 | Informar o payload completo atualizando o campo Bairro para `Chácara Cachoeira` e mantendo os demais campos com seus valores atuais. |
| 3 | Enviar a requisição para processamento. |
| 4 | Consultar o código de status HTTP retornado e a resposta. |
| 5 | Verificar na base de dados a persistência do novo bairro no endereço da localidade. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (mantido) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - mantido) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - mantido) |
| CEP | `79002-000` (mantido) |
| Logradouro | `Rua da Paz` (mantido) |
| Número | `140` (mantido) |
| Bairro | `Chácara Cachoeira` (Atualizado) |
| Complemento | `Edifício Sede` (mantido) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `true` (mantido) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | O campo Bairro no endereço da localidade é atualizado para `Chácara Cachoeira`. |
| 3 | Todos os demais campos permanecem inalterados e consistentes no banco de dados. |
| 4 | O payload retornado reflete as informações atualizadas em conformidade com o DTO. |

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
| Observações | Atualização cadastral de bairro conforme HU-10.3 e limites do DER (máximo 100 caracteres). |
