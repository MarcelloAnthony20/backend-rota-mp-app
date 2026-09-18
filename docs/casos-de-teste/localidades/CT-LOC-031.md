# Caso de Teste - Inativar localidade ativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-031 |
| **Nome do Caso de Teste** | Inativar localidade ativa |
| **Funcionalidade Testada** | Inativação de Localidade / Deleção Lógica |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Transição de Estado / Deleção Lógica |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que uma localidade com status ativo pode ser inativada via endpoint DELETE por um gestor autenticado, retornando confirmação de sucesso com status HTTP 204 No Content sem corpo de resposta e atualizando o status para inativo no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Localidade A existente com status ativo (`ativo = true`) e ID `l0c00001-0000-0000-0000-000000000001` cadastrada na base de dados. |
| 3 | Serviço de gerenciamento de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `DELETE /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` contendo o token de autorização do gestor no cabeçalho. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar que o corpo da resposta HTTP está vazio (sem conteúdo). |
| 4 | Consultar o registro na base de dados e verificar se a flag `ativo` foi alterada para `false` por meio de exclusão lógica (soft delete). |

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
| Status / Ativo | `true` (antes da deleção) |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `DELETE` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação de inativação aceita com sucesso retornando status HTTP `204 No Content`. |
| 2 | Corpo da resposta HTTP vazio (sem payload). |
| 3 | Status da localidade no banco de dados atualizado para inativo (`ativo = false`) mediante exclusão lógica (soft delete). |
| 4 | Histórico de atendimentos e movimentações associados preservado integralmente no banco de dados. |

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
| Observações | Alinhamento com a diretriz mandatória: deleção/inativação sempre retorna status HTTP 204 No Content (corpo vazio) conforme HU-10.4. |
