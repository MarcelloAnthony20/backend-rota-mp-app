# Caso de Teste - Buscar cidade por parte do nome sem correspondência - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-014 |
| **Nome do Caso de Teste** | Buscar cidade por parte do nome sem correspondência |
| **Funcionalidade Testada** | Consulta de cidade por nome parcial |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Busca sem Registro |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a consulta de cidade por filtro parcial de nome sem qualquer correspondência na base de dados retorna status HTTP 404 Not Found estruturado no formato ProblemDetail RFC 7807. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil operacional ou superior. |
| 2 | Serviço de consulta de cidades disponível e operacional. |
| 3 | Nenhuma cidade na base de dados possui o termo `InexistenteXYZ` em seu nome. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Realizar requisição GET para o endpoint de busca filtrada (`GET /api/v1/cidades?nome=InexistenteXYZ`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_usuario>`. |
| 3 | Consultar o código de status HTTP retornado na resposta da API. |
| 4 | Validar o corpo da resposta de erro estruturada. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | `""` (Não aplicável ao filtro) |
| Nome da Cidade | `InexistenteXYZ` (Termo sem correspondência) |
| Código da Cidade | `""` (Não aplicável ao filtro) |
| Status / Ativo | `""` (Não aplicável ao filtro) |
| Parâmetro de Consulta (Query Param) | `nome=InexistenteXYZ` |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `Bearer <token_usuario>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API retorna status HTTP `404 Not Found`. |
| 2 | Resposta de erro estruturada no padrão RFC 7807 (ProblemDetail) informando que nenhuma cidade foi encontrada para o filtro de nome informado. |
| 3 | Cabeçalho `Content-Type: application/problem+json` retornado na resposta. |
| 4 | Nenhuma informação interna do banco de dados ou exceção de runtime é exposta. |

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
| Observações | Aplicação da diretriz mandatória de negócio: toda busca textual sem correspondência retorna HTTP 404 Not Found com ProblemDetail RFC 7807 conforme HU-08.2. |
