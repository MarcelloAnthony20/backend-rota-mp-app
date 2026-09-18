# Caso de Teste - Buscar cidade por parte do nome com correspondência - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-015 |
| **Nome do Caso de Teste** | Buscar cidade por parte do nome com correspondência |
| **Funcionalidade Testada** | Consulta de cidade por nome parcial |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Case-Insensitive |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a busca por parte do nome retorna com sucesso as cidades correspondentes (retornando a Seed Cidade A - Campo Grande) de forma insensível a maiúsculas/minúsculas (case-insensitive). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil operacional ou superior. |
| 2 | Seed Cidade A cadastrada na base de dados com ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"`, `ativo = true`. |
| 3 | Serviço de busca de cidades disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Realizar requisição GET com o parâmetro de consulta `nome=Campo` (`GET /api/v1/cidades?nome=Campo`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_usuario>`. |
| 3 | Consultar o código de status HTTP retornado na resposta da API. |
| 4 | Conferir a lista de cidades retornadas no payload JSON. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Campo Grande` |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Parâmetro de Consulta (Query Param) | `nome=Campo` |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `Bearer <token_usuario>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | API retorna status HTTP `200 OK`. |
| 2 | A coleção retornada contém a Seed Cidade A com dados íntegros: `id: "c1d00001-0000-0000-0000-000000000001"`, `nome: "Campo Grande"`, `codigo: "5002704"` e `ativo: true`. |
| 3 | A filtragem é executada de forma case-insensitive, localizando correspondências parciais válidas. |
| 4 | Os registros retornados seguem estritamente a estrutura do DTO `CidadeResponse`. |

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
| Observações | Validação de busca parcial textual case-insensitive retornando a Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`) conforme HU-08.2. |
