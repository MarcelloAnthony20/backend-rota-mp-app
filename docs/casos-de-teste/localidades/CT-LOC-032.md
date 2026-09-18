# Caso de Teste - Ativar localidade inativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-032 |
| **Nome do Caso de Teste** | Ativar localidade inativa |
| **Funcionalidade Testada** | Reativação de Localidade / Transição de Estado |
| **Prioridade** | Alta |
| **Tipo de Teste** | Transição de Estado / Teste Positivo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que uma localidade com status inativo pode ser reativada por um gestor autenticado via endpoint PATCH, retornando status HTTP 200 OK e atualizando o status para ativo no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Localidade A existente com status inativo (`ativo = false`) e ID `l0c00001-0000-0000-0000-000000000001` cadastrada na base de dados. |
| 3 | Cidade vinculada (`c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada`) ativa no sistema. |
| 4 | Tipo de localidade vinculado (`t1p00001-0000-0000-0000-000000000001`) ativo no sistema. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `PATCH /api/v1/localidades/l0c00001-0000-0000-0000-000000000001/ativar` com cabeçalho de autorização do gestor. |
| 2 | Consultar o código de status HTTP retornado na resposta. |
| 3 | Inspecionar o corpo da resposta e verificar se o atributo `ativo` retornou como `true`. |
| 4 | Verificar na base de dados se a flag `ativo` foi persistida como `true`. |
| 5 | Conferir se a localidade volta a ser exibida nas listagens ativas para novos atendimentos. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `false` (antes da requisição) |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `PATCH` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação de reativação aceita com sucesso retornando status HTTP `200 OK`. |
| 2 | Payload de resposta apresenta o registro completo da localidade com campo `ativo = true`. |
| 3 | Registro no banco de dados atualizado com status ativo (`ativo = true`). |
| 4 | Localidade volta a estar disponível para seleção em novos deslocamentos e rotas operacionais. |

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
| Observações | Funcionalidade de reativação operacional de localidades inativadas conforme HU-10.4. |
