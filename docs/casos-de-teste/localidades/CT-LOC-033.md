# Caso de Teste - Inativar localidade já inativa - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-033 |
| **Nome do Caso de Teste** | Inativar localidade já inativa |
| **Funcionalidade Testada** | Idempotência da Inativação de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Idempotência / Transição de Estado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a requisição de inativação via método DELETE de uma localidade que já se encontra inativa é tratada de forma idempotente, retornando status HTTP 204 No Content sem corpo de resposta e mantendo o registro inativo no banco de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Localidade A existente previamente inativada com status `ativo = false` e ID `l0c00001-0000-0000-0000-000000000001`. |
| 3 | Serviço de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `DELETE /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` contendo cabeçalho de autorização do gestor. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar que o corpo da resposta HTTP está vazio (sem conteúdo). |
| 4 | Consultar o registro no banco de dados e confirmar que o status permaneceu inativo (`ativo = false`) sem efeitos colaterais ou alterações indevidas. |

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
| Status / Ativo | `false` (já inativa) |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `DELETE` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação tratada de forma idempotente retornando status HTTP `204 No Content`. |
| 2 | Corpo da resposta HTTP vazio (sem payload). |
| 3 | O registro permanece com status inativo (`ativo = false`) na base de dados. |
| 4 | Nenhuma duplicidade, inconsistência ou erro interno 500 é gerado pelo sistema. |

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
| Observações | Alinhamento com a diretriz mandatória: deleção/inativação sempre retorna status HTTP 204 No Content e critério de idempotência de HU-10.4. |
