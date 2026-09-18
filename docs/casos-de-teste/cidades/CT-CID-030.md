# Caso de Teste - Consultar cidade por ID com formato inválido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-030 |
| **Nome do Caso de Teste** | Consultar cidade por ID com formato inválido |
| **Funcionalidade Testada** | Consulta de cidade por ID / Validação de Parâmetro |
| **Prioridade** | Média |
| **Tipo de Teste** | Teste Negativo / Validação de Formato / Sintaxe |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de consultar uma cidade passando um identificador de rota com formato não conforme (ex.: `id-invalido-12345` em vez de UUID padrão) é rejeitada com status HTTP 400 Bad Request. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil autorizado. |
| 2 | Endpoint de consulta por ID `/api/v1/cidades/{id}` operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Enviar requisição `GET /api/v1/cidades/id-invalido-12345` acompanhada do token de autorização. |
| 2 | Consultar o código de status HTTP retornado pelo servidor. |
| 3 | Validar a estrutura da resposta de erro retornada. |
| 4 | Confirmar que nenhuma falha de conversão interna não tratada (HTTP 500) foi exposta. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | `id-invalido-12345` (Formato inválido / não UUID) |
| Nome da Cidade | `""` (Não aplicável na consulta) |
| Código da Cidade | `""` (Não aplicável na consulta) |
| Status / Ativo | `""` (Não aplicável na consulta) |
| Método HTTP | `GET` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 (ProblemDetail) informando incompatibilidade de formato do parâmetro de rota. |
| 3 | Nenhuma consulta indevida ao banco de dados ou erro de infraestrutura (HTTP 500) ocorre. |

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
| Observações | Atendimento ao critério de validação prévia de parâmetros de entrada na camada de controle (Controller) conforme RFC 7807 e HU-08.2. |

