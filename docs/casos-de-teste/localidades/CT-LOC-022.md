# Caso de Teste - Validar dropdowns de cidade e tipo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-022 |
| **Nome do Caso de Teste** | Validar dropdowns de cidade e tipo |
| **Funcionalidade Testada** | Seleção de Cidade e Tipo de Localidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Regra de Negócio / Validação de Listas |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que os endpoints e listas de seleção de Cidade e Tipo de Localidade para cadastro de localidade listam exclusivamente registros ativos, excluindo cidades inativas (como a Cidade B inativada). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Cidade A cadastrada e ativa (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Cidade B inativada/deletada com HTTP 204 (`c1d00002-0000-0000-0000-000000000002` - Dourados, `ativo = false`). |
| 4 | Tipo de localidade cadastrado e ativo (`b1000001-0000-0000-0000-000000000001` - Promotoria de Justiça). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de consulta de cidades para preenchimento de dropdowns (`GET /api/v1/cidades?ativo=true`). |
| 2 | Acessar o endpoint de consulta de tipos de localidade para preenchimento de dropdowns (`GET /api/v1/tipos-localidades?ativo=true`). |
| 3 | Consultar os códigos de status HTTP retornados e a lista de itens. |
| 4 | Verificar se a Cidade A ativa consta na listagem e se a Cidade B inativa foi filtrada/omitida. |
| 5 | Verificar se apenas tipos de localidade ativos constam na lista de opções. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | Não aplicável (consulta de opções para formulário de cadastro) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (contexto de preenchimento) |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - opção ativa) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - opção ativa) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Parâmetro de Consulta | `ativo=true` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `GET /api/v1/cidades` e `GET /api/v1/tipos-localidades` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Endpoints respondem com status HTTP `200 OK` contendo listas estruturadas em formato JSON. |
| 2 | A listagem de cidades inclui a Cidade A (`c1d00001-0000-0000-0000-000000000001`) e não inclui a Cidade B inativada (`c1d00002-0000-0000-0000-000000000002`). |
| 3 | A listagem de tipos de localidade inclui apenas registros ativos (`ativo = true`), como o Tipo 1 (`b1000001-0000-0000-0000-000000000001`). |
| 4 | O usuário não tem acesso a registros inativos para criar novas associações no sistema. |

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
| Observações | Atendimento aos critérios de HU-08.4, HU-09.4 e HU-10.1 para integridade das listas de seleção. |
