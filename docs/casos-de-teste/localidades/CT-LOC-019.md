# Caso de Teste - Adicionar localidade duplicada - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-019 |
| **Nome do Caso de Teste** | Adicionar localidade duplicada |
| **Funcionalidade Testada** | Unicidade de Localidade (RN-6) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Unicidade |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema impede o cadastro de localidade duplicada com a mesma combinação de Cidade, Tipo de Localidade, Nome e Endereço já existente, retornando HTTP 409 Conflict. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A (`l0c00001-0000-0000-0000-000000000001` - Promotoria de Justiça da Cidadania) já cadastrada e vinculada à Cidade A (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada) e Tipo (`t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça). |
| 3 | Serviço de cadastro de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de cadastro de localidade (`POST /api/v1/localidades`). |
| 2 | Informar payload duplicado com os mesmos dados da Localidade A já existente. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a mensagem de erro. |
| 5 | Verificar na base de dados que nenhum registro duplicado foi gravado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Gerado automaticamente pelo sistema (nulo no envio) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `POST /api/v1/localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `409 Conflict`. |
| 2 | Mensagem de erro informativa (ProblemDetail RFC 7807) indicando violação de regra de unicidade para a localidade informada (RN-6). |
| 3 | Nenhum registro duplicado é persistido na tabela `localidade` ou na tabela de endereços. |
| 4 | A integridade e unicidade dos registros são mantidas. |

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
| Observações | Atendimento estrito à Regra de Negócio RN-6 e critério de aceitação de HU-10.1. |
