# Caso de Teste - Consultar localidade por ID inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-038 |
| **Nome do Caso de Teste** | Consultar localidade por ID inexistente |
| **Funcionalidade Testada** | Consulta de Localidade por ID |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Recurso Não Encontrado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a consulta de localidade informando um identificador UUID sintaticamente válido mas inexistente na base de dados (l0c99999-9999-9999-9999-999999999999) retorna status HTTP 404 Not Found no formato RFC 7807 (ProblemDetail). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil autorizado (Gestor ou Operador). |
| 2 | Identificador UUID `l0c99999-9999-9999-9999-999999999999` não cadastrado na base de dados de localidades. |
| 3 | Serviço de consulta de localidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `GET /api/v1/localidades/l0c99999-9999-9999-9999-999999999999` com token de autorização válido. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar o corpo da resposta em conformidade estrita com o formato ProblemDetail (RFC 7807). |
| 4 | Confirmar que nenhuma exceção de infraestrutura ou stack trace foi exposto. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c99999-9999-9999-9999-999999999999` (Inexistente) |
| Nome da Localidade | `Localidade Inexistente` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `false` |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `GET` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação retorna status HTTP `404 Not Found`. |
| 2 | Resposta estruturada conforme especificação RFC 7807 (ProblemDetail) contendo `title`, `status`, `detail` e `instance`. |
| 3 | Mensagem de erro informa claramente que a localidade com o ID informado não foi encontrada. |
| 4 | Nenhum vazamento de informações internas, exceções ou stack trace ocorre na resposta. |

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
| Observações | Alinhamento com a diretriz mandatória: toda busca sem encontrar deve retornar HTTP 404 Not Found em formato RFC 7807 conforme HU-10.2. |
