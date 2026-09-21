# Caso de Teste - Classificar localidade com tipo inativo ou inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-056 |
| **Nome do Caso de Teste** | Classificar localidade com tipo inativo ou inexistente |
| **Funcionalidade Testada** | Classificação de Localidade / Integridade de FK (HU-10.5) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Integridade de FK / Regra de Negócio |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de classificar uma localidade vinculando-a a um tipo inativo (`b1000009-0000-0000-0000-000000000009`) ou inexistente (`00000000-0000-0000-0000-000000000999`) é bloqueada com retorno HTTP 400 Bad Request / 422 Unprocessable Entity ou HTTP 404 Not Found (ProblemDetail RFC 7807), mantendo intacto o tipo anterior da localidade. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Localidade A existente cadastrada e vinculada ao tipo ativo original (`d1000001-0000-0000-0000-000000000001`). |
| 3 | Tipo de localidade inativo existente (ID: `b1000009-0000-0000-0000-000000000009`, `ativo = false`). |
| 4 | Identificador de tipo de localidade inexistente (`00000000-0000-0000-0000-000000000999`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `PATCH /api/v1/localidades/d1000001-0000-0000-0000-000000000001/classificacao` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar payload com o ID de tipo inativo: `{"tipoLocalidadeId": "b1000009-0000-0000-0000-000000000009"}`. |
| 3 | Enviar a requisição e consultar o status retornado e payload de erro RFC 7807. |
| 4 | Repetir a operação informando ID de tipo inexistente (`00000000-0000-0000-0000-000000000999`) e verificar retorno HTTP 404 Not Found. |
| 5 | Consultar a base de dados para garantir que a associação original da localidade (`b1000001-0000-0000-0000-000000000001`) foi preservada sem modificações. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000009-0000-0000-0000-000000000009` (Inativo) / `00000000-0000-0000-0000-000000000999` (Inexistente) |
| CEP | `79004-311` |
| Logradouro | `Rua Ceará` |
| Número | `800` |
| Bairro | `Santa Fé` |
| Complemento | `Bloco B` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `PATCH /api/v1/localidades/d1000001-0000-0000-0000-000000000001/classificacao` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Para tipo inativo: operação rejeitada com status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Para tipo inexistente: operação rejeitada com status HTTP `404 Not Found` em conformidade com RFC 7807 (ProblemDetail). |
| 3 | Mensagem de erro descreve que o tipo informado deve existir e estar ativo. |
| 4 | O vínculo anterior da localidade é mantido intacto sem sofrer qualquer alteração na base de dados. |

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
| Observações | Atendimento ao critério de integridade de HU-10.5: bloquear a operação e manter o tipo anterior inalterado quando informado tipo inativo ou inexistente. |
