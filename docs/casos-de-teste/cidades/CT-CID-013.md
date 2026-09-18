# Caso de Teste - Buscar Cidade A por ID existente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-013 |
| **Nome do Caso de Teste** | Buscar Cidade A por ID existente |
| **Funcionalidade Testada** | Consulta de cidade por ID |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste Positivo / Consulta Pontual |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema retorna os dados corretos e estruturados via DTO ao consultar a Seed Cidade A informando seu identificador único cadastrado (`c1d00001-0000-0000-0000-000000000001`). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil operacional ou superior. |
| 2 | Seed Cidade A cadastrada previamente no banco de dados com ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"` e status ativo (`true`). |
| 3 | Serviço de consulta de cidades disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Realizar requisição GET para o endpoint da Seed Cidade A (`GET /api/v1/cidades/c1d00001-0000-0000-0000-000000000001`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_usuario>`. |
| 3 | Consultar o código de status HTTP retornado. |
| 4 | Validar a integridade e precisão dos atributos retornados no corpo da resposta. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande` |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `Bearer <token_usuario>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API retorna status HTTP `200 OK`. |
| 2 | Payload de resposta estritamente estruturado conforme DTO `CidadeResponse` contendo `id: "c1d00001-0000-0000-0000-000000000001"`, `nome: "Campo Grande"`, `codigo: "5002704"` e `ativo: true`. |
| 3 | Nenhum atributo interno de infraestrutura ou entidade JPA é exposto indevidamente. |
| 4 | Tempo de resposta dentro do SLA aceitável (< 500ms). |

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
| Observações | Consulta pontual por ID da entidade canônica Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`) com validação do contrato de resposta DTO conforme HU-08.2. |
