# Caso de Teste - Excluir localidade com DELETE - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-044 |
| **Nome do Caso de Teste** | Excluir localidade com DELETE |
| **Funcionalidade Testada** | Exclusão / Inativação de Localidade (Soft Delete) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste de Contrato / Deleção Lógica / Idempotência |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a exclusão de localidade via requisição DELETE executa a exclusão lógica (soft delete) com retorno HTTP 204 No Content com corpo de resposta vazio, preservando o registro e a integridade referencial no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Localidade A existente com status ativo (`ativo = true`) e ID `d1000001-0000-0000-0000-000000000001` cadastrada na base de dados. |
| 3 | Localidade possui histórico prévio de atendimentos e rotas associadas. |
| 4 | Serviço de gerenciamento de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `DELETE /api/v1/localidades/d1000001-0000-0000-0000-000000000001` acompanhada do token de autorização do gestor. |
| 2 | Consultar o código de status HTTP retornado na resposta. |
| 3 | Validar que o corpo da resposta HTTP está estritamente vazio (No Content). |
| 4 | Consultar o banco de dados e verificar que a linha correspondente NÃO foi fisicamente excluída (sem hard delete), mas teve o atributo `ativo` atualizado para `false`. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` (antes da exclusão) |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `DELETE` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `204 No Content`. |
| 2 | Corpo de resposta HTTP sem conteúdo (vazio). |
| 3 | O registro da localidade é preservado no banco de dados com status inativo (`ativo = false`) por exclusão lógica. |
| 4 | Nenhuma exclusão física (hard delete) é efetuada, mantendo íntegro todo o histórico de movimentações, auditorias e relatórios do MPMS. |

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
| Observações | Alinhamento com a diretriz mandatória: deleção/inativação sempre retorna status HTTP 204 No Content (corpo vazio) e garantia de integridade histórica via soft delete conforme HU-10.4. |
