# Caso de Teste - Alterar cidade para ID inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-LOC-028 |
| **Nome do Caso de Teste** | Alterar cidade para ID inexistente |
| **Funcionalidade Testada** | Alteração de Localidade / Integridade Referencial |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Integridade Referencial |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a alteração de localidade vinculando um identificador de cidade inexistente (`c1d99999-9999-9999-9999-999999999999`) é rejeitada, mantendo o vínculo original intacto. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Localidade A cadastrada (`l0c00001-0000-0000-0000-000000000001`) vinculada à Cidade A (`c1d00001-0000-0000-0000-000000000001` - Campo Grande Alterada). |
| 3 | Identificador de cidade `c1d99999-9999-9999-9999-999999999999` inexistente na base de dados. |
| 4 | Serviço de alteração de localidade operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de localidade (`PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001`). |
| 2 | Informar o payload completo atribuindo o ID inexistente `c1d99999-9999-9999-9999-999999999999` ao campo Cidade (FK) e mantendo os outros 10 campos. |
| 3 | Enviar a requisição para a API. |
| 4 | Consultar o código de status HTTP retornado e o detalhe do erro. |
| 5 | Conferir no banco de dados se a Cidade original (`c1d00001-0000-0000-0000-000000000001`) foi preservada na localidade. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Localidade | `l0c00001-0000-0000-0000-000000000001` |
| Nome da Localidade | `Promotoria de Justiça da Cidadania` (mantido) |
| Cidade (FK) | `c1d99999-9999-9999-9999-999999999999` (ID de cidade inexistente) |
| Tipo de Localidade (FK) | `t1p00001-0000-0000-0000-000000000001` (Promotoria de Justiça - mantido) |
| CEP | `79002-000` (mantido) |
| Logradouro | `Rua da Paz` (mantido) |
| Número | `140` (mantido) |
| Bairro | `Jardim dos Estados` (mantido) |
| Complemento | `Edifício Sede` (mantido) |
| Local Sigiloso | `false` (mantido) |
| Status / Ativo | `true` (mantido) |
| Cabeçalho Authorization | `Bearer <token_gestor>` |
| Método HTTP | `PUT /api/v1/localidades/l0c00001-0000-0000-0000-000000000001` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `404 Not Found` (ProblemDetail RFC 7807) ou `422 Unprocessable Entity` / `400 Bad Request`. |
| 2 | Mensagem de erro informativa indica que a cidade informada não existe ou não foi encontrada no cadastro. |
| 3 | A integridade referencial é preservada e o vínculo original com a Cidade A (`c1d00001-0000-0000-0000-000000000001`) permanece inalterado. |
| 4 | Nenhum dado da localidade é corrompido ou parcialmente modificado no banco de dados. |

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
| Observações | Atendimento ao critério de integridade referencial de HU-10.3: "validar que a cidade informada exista e esteja ativa". |
