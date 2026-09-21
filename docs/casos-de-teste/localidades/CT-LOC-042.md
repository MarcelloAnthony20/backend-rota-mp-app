# Caso de Teste - Consultar localidade com ID inválido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-042 |
| **Nome do Caso de Teste** | Consultar localidade com ID inválido |
| **Funcionalidade Testada** | Consulta de Localidade por ID / Validação de Identificador |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Identificador |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de consultar uma localidade passando um identificador em formato não-UUID (id-invalido-loc) na rota é rejeitada com status HTTP 400 Bad Request no formato RFC 7807, sem expor exceções de conversão. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil autorizado (Gestor ou Operador). |
| 2 | Endpoint `GET /api/v1/localidades/{id}` disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `GET /api/v1/localidades/id-invalido-loc` com token de autorização. |
| 2 | Consultar o código de status HTTP retornado na resposta da requisição. |
| 3 | Validar a mensagem de erro retornada no formato ProblemDetail (RFC 7807). |
| 4 | Confirmar que nenhuma exceção de conversão de tipos (ex.: MethodArgumentTypeMismatchException) ou stack trace foi exposto. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `id-invalido-loc` (Formato não-UUID inválido) |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` |
| Cidade (FK) | `c1d00001-0000-0000-0000-000000000001 - Campo Grande Alterada` |
| Tipo de Localidade (FK) | `b1000001-0000-0000-0000-000000000001 - Promotoria de Justiça` |
| CEP | `79002-000` |
| Logradouro | `Rua da Paz` |
| Número | `140` |
| Bairro | `Jardim dos Estados` |
| Complemento | `Edifício Sede` |
| Local Sigiloso | `false` |
| Status / Ativo | `true` |
| Cabeçalho Authorization | `Bearer <token_jwt_gestor_valido>` |
| Perfil do Usuário | `Gestor` |
| Método HTTP | `GET` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Requisição rejeitada pelo validador de parâmetros retornando status HTTP `400 Bad Request`. |
| 2 | Resposta estruturada conforme especificação RFC 7807 (ProblemDetail) detalhando formato inválido de identificador UUID. |
| 3 | Nenhum detalhe interno do conversor de tipo ou stack trace é vazado ao cliente. |

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
| Observações | Validação de restrição de formato de parâmetro de rota UUID conforme HU-10.2 e boas práticas REST. |
