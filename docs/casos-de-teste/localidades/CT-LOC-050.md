# Caso de Teste - Consultar listagem geral de localidades - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-050 |
| **Nome do Caso de Teste** | Consultar listagem geral de localidades |
| **Funcionalidade Testada** | Consulta de Localidades / Paginação (HU-10.2) |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Consulta Geral e Paginação |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que usuário autenticado com perfil com permissão de leitura consegue consultar a listagem geral paginada de localidades com status HTTP 200 OK, recebendo o contrato estruturado conforme `LocalidadeResponse` em DTO. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado e possuidor de permissão de leitura de localidades. |
| 2 | Localidades previamente cadastradas no sistema, incluindo a Localidade A (`d1000001-0000-0000-0000-000000000001`). |
| 3 | Paginação configurada e ativa na API de localidades. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Preparar requisição `GET /api/v1/localidades?page=0&size=10` com cabeçalho `Authorization: Bearer <token_usuario>`. |
| 2 | Enviar a requisição para o endpoint de listagem. |
| 3 | Consultar o código de status HTTP retornado. |
| 4 | Validar o corpo da resposta: conferir a coleção paginada, campos da entidade e metadados de paginação. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `d1000001-0000-0000-0000-000000000001` (Registro presente na base) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001` (Campo Grande Alterada - Ativa) |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001` (Promotoria de Justiça - Ativo) |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_usuario>` |
| Perfil do Usuário | Gestor / Usuário com permissão de leitura |
| Método HTTP | `GET /api/v1/localidades?page=0&size=10` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API responde com código de status HTTP `200 OK`. |
| 2 | Resposta estruturada contendo coleção paginada de `LocalidadeResponse` com todos os atributos públicos da localidade. |
| 3 | Metadados de navegação e paginação preenchidos corretamente (`totalPages`, `totalElements`, `size`, `number`). |
| 4 | Nenhuma entidade JPA ou dado confidencial não autorizado é vazado no payload. |

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
| Observações | Atendimento ao critério de listagem paginada e encapsulamento em DTO de HU-10.2. |
