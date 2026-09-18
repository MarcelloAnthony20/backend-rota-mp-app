# Caso de Teste - Usar método HTTP não permitido na inclusão - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-039 |
| **Nome do Caso de Teste** | Usar método HTTP não permitido na inclusão |
| **Funcionalidade Testada** | Cadastro de Localidade / Contrato HTTP |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste de Contrato / Método HTTP Não Permitido |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de submeter requisição de inclusão de localidade utilizando método HTTP não permitido (método PUT na rota de coleção /api/v1/localidades) é rejeitada com status HTTP 405 Method Not Allowed e cabeçalho Allow com os métodos suportados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor (`ROLE_GESTOR`). |
| 2 | Rota de coleção `/api/v1/localidades` configurada para aceitar unicamente métodos POST e GET. |
| 3 | Serviço de API de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `PUT` para a URL de coleção `/api/v1/localidades` contendo payload com dados de criação de localidade. |
| 2 | Consultar o código de status HTTP retornado na resposta. |
| 3 | Inspecionar os cabeçalhos de resposta HTTP e verificar a presença do cabeçalho `Allow`. |
| 4 | Consultar o banco de dados e garantir que nenhum registro foi criado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `""` (Não gerado) |
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
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `PUT` (Incorreto para coleção) |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo servidor retornando status HTTP `405 Method Not Allowed`. |
| 2 | Cabeçalho HTTP `Allow` retornado explicitando os métodos permitidos para a rota de coleção (ex.: `POST, GET`). |
| 3 | Nenhum dado é inserido ou modificado na base de dados. |

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
| Observações | Validação de conformidade com os contratos REST e tratamento adequado de métodos HTTP conforme HU-10.1. |
