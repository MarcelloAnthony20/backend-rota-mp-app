# Caso de Teste - Consultar localidade sem autenticação - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-035 |
| **Nome do Caso de Teste** | Consultar localidade sem autenticação |
| **Funcionalidade Testada** | Consulta de Localidade / Autenticação |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Segurança / Autenticação |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a requisição de consulta de localidade enviada sem cabeçalho de autenticação (sem token Authorization) é sumariamente rejeitada pelo filtro de segurança com status HTTP 401 Unauthorized. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Serviço da API de Localidades em execução e protegido por filtro de autenticação Bearer JWT. |
| 2 | Localidade A existente com ID `l0c00001-0000-0000-0000-000000000001` cadastrada na base de dados. |
| 3 | Requisição enviada sem nenhum cabeçalho `Authorization`. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `GET /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` sem fornecer o cabeçalho `Authorization`. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar que o acesso foi bloqueado e nenhum dado cadastral da localidade foi exposto. |

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
| Status / Ativo | `true` |
| Cabeçalho Authorization | `""` (Ausente) |
| Perfil do Usuário | `Não autenticado (Anônimo)` |
| Método HTTP | `GET` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição bloqueada retornando status HTTP `401 Unauthorized`. |
| 2 | Mensagem de erro informativa indicando a necessidade de credencial de autenticação válida para acessar o recurso. |
| 3 | Nenhum atributo ou dado da localidade é exposto na resposta da API. |

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
| Observações | Proteção de endpoint contra acesso público não autenticado conforme requisitos de segurança da arquitetura e HU-10.2. |
