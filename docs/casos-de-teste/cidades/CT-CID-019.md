# Caso de Teste - Inativar cidade usando PATCH em vez de DELETE - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-019 |
| **Nome do Caso de Teste** | Inativar cidade usando PATCH em vez de DELETE |
| **Funcionalidade Testada** | Inativação de cidade e método HTTP |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste de Contrato / Método HTTP / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o endpoint de alteração/inativação por identificador rejeita o método HTTP PATCH quando enviado na rota base de cidade (`/api/v1/cidades/{id}`), retornando status HTTP 405 Method Not Allowed e assegurando a conformidade do contrato RESTful onde inativação ocorre estritamente via DELETE. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade B cadastrada e ativa na base de dados (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`, `ativo = true`). |
| 3 | API de Cidades do Rota MP APP operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint informando o identificador da Seed Cidade B (`/api/v1/cidades/c1d00002-0000-0000-0000-000000000002`). |
| 2 | Enviar requisição utilizando o método HTTP `PATCH` acompanhada do token de autorização de Gestor. |
| 3 | Consultar o código de status HTTP retornado e os cabeçalhos da resposta. |
| 4 | Verificar se os dados e o estado da Seed Cidade B permanecem inalterados no banco de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade B |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Ativo (true)` |
| Método HTTP | `PATCH` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | A requisição é rejeitada com status HTTP `405 Method Not Allowed`. |
| 2 | Cabeçalho `Allow` é retornado informando os métodos HTTP suportados pelo endpoint (`GET, PUT, DELETE`). |
| 3 | Os dados e status da Seed Cidade B permanecem inalterados no banco de dados. |
| 4 | Nenhum erro inesperado de servidor (HTTP 500) é gerado. |

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
| Observações | Validação de conformidade com os verbos HTTP RESTful e integridade de contrato conforme HU-08.4. Utiliza explicitamente a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`). |
