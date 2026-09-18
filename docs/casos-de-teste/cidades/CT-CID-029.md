# Caso de Teste - Consultar listagem geral de cidades cadastradas - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-029 |
| **Nome do Caso de Teste** | Consultar listagem geral de cidades cadastradas |
| **Funcionalidade Testada** | Listagem de Cidades / Consulta Geral |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Consulta Geral / Linhagem de Dados |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o usuário autenticado e autorizado consegue consultar a listagem de cidades cadastradas, recebendo status HTTP 200 OK com coleção contendo as seeds fixas cadastradas no sistema (Seed Cidade A, Seed Cidade B, Seed Cidade C e Seed Cidade D), estruturadas estritamente via DTO `CidadeResponse`. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil autorizado (Gestor ou Operador). |
| 2 | Seed Cidade A existente na base de dados (ID `c1d00001-0000-0000-0000-000000000001`, Código `5002704`). |
| 3 | Seed Cidade B existente na base de dados (ID `c1d00002-0000-0000-0000-000000000002`, Código `5003702`). |
| 4 | Seed Cidade C existente na base de dados (ID `c1d00003-0000-0000-0000-000000000003`, Código `5008306`). |
| 5 | Seed Cidade D (Inativa) existente na base de dados (ID `c1d00004-0000-0000-0000-000000000004`, Código `5003207`, `ativo = false`). |
| 6 | Endpoint de consulta de cidades operacional (`GET /api/v1/cidades`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Executar requisição com método `GET` para o endpoint de listagem de cidades (`GET /api/v1/cidades`). |
| 2 | Consultar o código de status HTTP retornado pelo servidor. |
| 3 | Validar a presença e os atributos das seeds cadastradas no corpo da resposta (`id`, `nome`, `codigo`, `ativo`). |
| 4 | Conferir se a estrutura da resposta respeita integralmente o contrato DTO (`CidadeResponse`) sem exposição de campos de infraestrutura. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidades Alvo | Seeds Fixas: Cidade A (`c1d00001`), Cidade B (`c1d00002`), Cidade C (`c1d00003`) e Cidade D (`c1d00004`) |
| Parâmetro de Consulta | `""` (Nenhum / listagem paginada geral) |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API retorna status HTTP `200 OK`. |
| 2 | Resposta estruturada como coleção/página JSON contendo instâncias do DTO `CidadeResponse`. |
| 3 | Seeds fixas presentes na listagem com seus respectivos identificadores e códigos IBGE devidamente mapeados. |
| 4 | Nenhuma entidade de persistência JPA ou dado confidencial de infraestrutura é exposto. |

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
| Observações | Confirmação da linhagem de dados com as 4 seeds fixas cadastradas pelo `CidadeDataInitializer`: Seed A (`c1d00001-0000-0000-0000-000000000001`), Seed B (`c1d00002-0000-0000-0000-000000000002`), Seed C (`c1d00003-0000-0000-0000-000000000003`) e Seed D (`c1d00004-0000-0000-0000-000000000004`) conforme HU-08.2. |
