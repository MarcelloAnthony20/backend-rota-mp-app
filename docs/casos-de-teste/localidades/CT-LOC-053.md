# Caso de Teste - Alterar localidade para tipo de localidade inativo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-053 |
| **Nome do Caso de Teste** | Alterar localidade para tipo de localidade inativo |
| **Funcionalidade Testada** | Alteração de Localidade / Integridade de Vínculo de Tipo (HU-10.3) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Integridade Referencial |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de alterar uma localidade vinculando-a a um tipo de localidade inativo (`b1000009-0000-0000-0000-000000000009` - Posto Avançado Desativado) é rejeitada com código HTTP 400 Bad Request ou 422 Unprocessable Entity, mantendo intacta a associação anterior com o tipo ativo. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Localidade A existente no sistema (ID: `d1000001-0000-0000-0000-000000000001`, associada ao Tipo Ativo `b1000001-0000-0000-0000-000000000001`). |
| 3 | Tipo de localidade inativo existente no sistema (ID: `b1000009-0000-0000-0000-000000000009`, `ativo = false`). |
| 4 | Regra de integridade referencial ativa para operações de alteração cadastral. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar requisição `PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Preencher o payload substituindo a FK de tipo pelo ID do tipo inativo `b1000009-0000-0000-0000-000000000009`, preservando os demais dados válidos. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e a resposta em RFC 7807 (ProblemDetail). |
| 5 | Consultar a base de dados para certificar que a localidade permaneceu associada ao tipo ativo original (`b1000001-0000-0000-0000-000000000001`). |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000009-0000-0000-0000-000000000009` (Posto Avançado Desativado - Inativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `PUT /api/v1/localidades/d1000001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada retornando código de status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta RFC 7807 (ProblemDetail) informando que o tipo de localidade selecionado deve estar obrigatoriamente ativo. |
| 3 | A localidade permanece associada ao Tipo de Localidade original (`b1000001-0000-0000-0000-000000000001`) sem nenhuma alteração no banco de dados. |

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
| Observações | Atendimento ao critério de integridade de HU-10.3: validar que a cidade e o tipo informados na alteração existam e estejam com status ativo. |
