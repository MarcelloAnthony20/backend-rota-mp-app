# Caso de Teste - Classificar localidade vinculando a tipo ativo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-055 |
| **Nome do Caso de Teste** | Classificar localidade vinculando a tipo ativo |
| **Funcionalidade Testada** | Classificação de Localidade (HU-10.5) |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste Positivo / Classificação por FK |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o gestor consegue classificar uma localidade vinculando-a ao identificador de um tipo de localidade existente e ativo (`t1p00001-0000-0000-0000-000000000001` - Promotoria de Justiça), persistindo a associação por chave estrangeira com status HTTP 200 OK. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor. |
| 2 | Localidade A existente no sistema (ID: `l0c00001-0000-0000-0000-000000000001`). |
| 3 | Tipo de localidade ativo cadastrado no sistema (ID: `t1p00001-0000-0000-0000-000000000001`, Nome: `Promotoria de Justiça`, `ativo = true`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar a requisição `PATCH /api/v1/localidades/l0c00001-0000-0000-0000-000000000001/classificacao` com cabeçalho `Authorization: Bearer <token_gestor>`. |
| 2 | Informar payload contendo `{"tipoLocalidadeId": "t1p00001-0000-0000-0000-000000000001"}`. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado. |
| 5 | Consultar o banco de dados e verificar o relacionamento na coluna de chave estrangeira `tipo_localidade_id`. |

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
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Perfil do Usuário | Gestor |
| Método HTTP | `PATCH /api/v1/localidades/l0c00001-0000-0000-0000-000000000001/classificacao` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando código de status HTTP `200 OK` (ou `204 No Content`). |
| 2 | Relacionamento FK atualizado com o identificador do tipo de localidade informado. |
| 3 | Resposta confirma os dados atualizados sem modificar campos de endereço ou nome da localidade. |
| 4 | Registro mantido íntegro e ativo no sistema. |

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
| Observações | Atendimento ao critério de classificação por chave estrangeira estabelecido em HU-10.5. |
