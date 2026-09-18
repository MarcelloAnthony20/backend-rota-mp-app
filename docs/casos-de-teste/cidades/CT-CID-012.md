# Caso de Teste - Buscar cidade por ID inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-012 |
| **Nome do Caso de Teste** | Buscar cidade por ID inexistente |
| **Funcionalidade Testada** | Consulta de cidade por ID |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Recurso Não Encontrado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a consulta de cidade informando um identificador único inexistente (`99999999-9999-9999-9999-999999999999`) retorna status HTTP 404 Not Found estruturado no formato ProblemDetail RFC 7807 sem expor dados internos do sistema. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil operacional ou superior. |
| 2 | Serviço de consulta de cidades disponível e operacional. |
| 3 | O ID `99999999-9999-9999-9999-999999999999` comprovadamente não existe na base de dados. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Realizar requisição GET para o endpoint de busca por ID (`GET /api/v1/cidades/99999999-9999-9999-9999-999999999999`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_usuario>`. |
| 3 | Consultar o código de status HTTP retornado. |
| 4 | Avaliar a estrutura do corpo da mensagem de erro retornada. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | `99999999-9999-9999-9999-999999999999` (Inexistente) |
| Nome da Cidade | `""` (Inexistente na base) |
| Código da Cidade | `""` (Inexistente na base) |
| Status / Ativo | `""` (Inexistente na base) |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `Bearer <token_usuario>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API retorna status HTTP `404 Not Found`. |
| 2 | Resposta estruturada conforme especificação RFC 7807 (ProblemDetail) informando que a cidade correspondente ao ID informado não foi encontrada. |
| 3 | Nenhum stack trace, informação de infraestrutura ou detalhe de banco de dados é exposto na resposta. |
| 4 | Resposta com cabeçalho `Content-Type: application/problem+json`. |

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
| Observações | Alinhado com a diretriz mandatória: toda busca sem encontrar deve retornar HTTP 404 Not Found com ProblemDetail RFC 7807 conforme HU-08.2. Utiliza o UUID padrão de recurso inexistente `99999999-9999-9999-9999-999999999999`. |
