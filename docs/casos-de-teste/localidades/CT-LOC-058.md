# Caso de Teste - Configurar sigilo com valor não booleano - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-058 |
| **Nome do Caso de Teste** | Configurar sigilo com valor não booleano |
| **Funcionalidade Testada** | Configurar Sigilo de Localidade / Validação de Tipagem (HU-10.6) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Tipagem / Schema Validation |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de configurar o sigilo de uma localidade enviando valor de tipo não booleano (ex.: string `"sim"`, número `1` ou texto arbitrário) é rejeitada com código HTTP 400 Bad Request em formato RFC 7807, sem alterar o estado da base de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Localidade A existente previamente cadastrada no sistema (ID: `l0c00001-0000-0000-0000-000000000001`). |
| 3 | Validador de schema e deserializador JSON ativo na API. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar requisição `PATCH /api/v1/localidades/l0c00001-0000-0000-0000-000000000001/sigilo` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar payload contendo valor em formato string não booleana: `{"localSigiloso": "sim"}`. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e o objeto ProblemDetail (RFC 7807). |
| 5 | Confirmar no banco de dados que a flag `local_sigiloso` e os demais dados permaneceram inalterados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79004-311` |
| Logradouro | `Rua Ceará` |
| Número | `800` |
| Bairro | `Santa Fé` |
| Complemento | `Bloco B` |
| Local Sigiloso | `"sim"` (Valor inválido, não booleano) |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `PATCH /api/v1/localidades/l0c00001-0000-0000-0000-000000000001/sigilo` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada retornando código de status HTTP `400 Bad Request`. |
| 2 | Resposta estruturada em ProblemDetail (RFC 7807) indicando erro de validação/deserialização: o campo `localSigiloso` exige estritamente tipo booleano (`true` ou `false`). |
| 3 | Nenhuma alteração é persistida na base de dados. |

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
| Observações | Atendimento ao critério estrito de validação de tipagem de HU-10.6: exigir tipo booleano estrito para a flag de sigilo, rejeitando strings e tipos incompatíveis com HTTP 400. |
