# Caso de Teste - Alterar cidade com ID inexistente - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-033 |
| **Nome do Caso de Teste** | Alterar cidade com ID inexistente |
| **Funcionalidade Testada** | Alteração de cidade / Recurso não encontrado |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Recurso Não Encontrado |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de alterar os dados de uma cidade informando um identificador inexistente na base de dados (`99999999-9999-9999-9999-999999999999`) retorna erro HTTP 404 Not Found estruturado via RFC 7807 (ProblemDetail). |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | O identificador `99999999-9999-9999-9999-999999999999` comprovadamente não existe no catálogo de cidades cadastradas. |
| 3 | Endpoint de alteração de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Montar payload JSON (`AtualizarCidadeRequest`) contendo dados válidos para atualização (`nome`, `codigo`, `ativo`), sem campo ID no corpo. |
| 2 | Enviar requisição `PUT /api/v1/cidades/99999999-9999-9999-9999-999999999999` acompanhada do token de autorização de Gestor. |
| 3 | Consultar o código de status HTTP retornado pelo servidor. |
| 4 | Validar se o corpo da resposta atende ao padrão RFC 7807 (ProblemDetail). |
| 5 | Confirmar que nenhum registro novo ou alteração foi gerada no banco de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade (Path Variable) | `99999999-9999-9999-9999-999999999999` (Inexistente) |
| Nome da Cidade | `Cidade Fantasma Alterada` |
| Código da Cidade | `5009999` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`AtualizarCidadeRequest`) | `{"nome": "Cidade Fantasma Alterada", "codigo": "5009999", "ativo": true}` (sem ID no corpo) |
| Método HTTP | `PUT` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `404 Not Found`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 (ProblemDetail) informando que o identificador da cidade a ser alterada não foi encontrado. |
| 3 | Nenhuma alteração ou criação de registro é realizada no banco de dados. |
| 4 | O sistema mantém a estabilidade sem disparar erros 500. |

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
| Observações | Atendimento à diretriz mandatória de retorno HTTP 404 Not Found (ProblemDetail RFC 7807) para recurso não localizado conforme HU-08.3. Utiliza o UUID padrão de recurso inexistente `99999999-9999-9999-9999-999999999999`. |
