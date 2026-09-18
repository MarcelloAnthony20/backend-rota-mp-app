# Caso de Teste - Adicionar cidade com 201 caracteres no nome - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-006 |
| **Nome do Caso de Teste** | Adicionar cidade com 201 caracteres no nome |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Valor Limite / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a rejeição do cadastro quando o campo nome possui 201 caracteres no payload `CriarCidadeRequest`, imediatamente acima do limite máximo de 200 caracteres, impedindo truncamentos ou estouros de buffer. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades disponível e operacional. |
| 3 | Código `5008307` disponível na base de dados (não conflitante com as seeds fixas). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON contendo apenas os campos `nome` (com 201 caracteres), `codigo` (`"5008307"`) e `ativo` (`true`), conforme `CriarCidadeRequest`. O envio é feito sem ID. |
| 4 | Consultar o código de resposta HTTP retornado e a mensagem de validação no corpo da resposta. |
| 5 | Consultar a base de dados para garantir que nenhum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Cidade Modelo de Testes Homologados para Validar Limite Superior Extremo com Exatamente Duzentos Caracteres Alfanuméricos Permitidos nas Regras de Validação Cadastral do Sistema Rota MP APP no Estado X` (201 caracteres) |
| Código da Cidade | `5008307` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Cidade Modelo...Estado X", "codigo": "5008307", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro informativa no formato RFC 7807 (ProblemDetail) indicando violação do limite máximo de 200 caracteres para o campo nome. |
| 3 | Nenhum registro é persistido na base de dados (atomicidade preservada). |
| 4 | Nenhuma exceção interna de banco de dados ou erro de infraestrutura é exposto ao consumidor da API. |

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
| Observações | Teste de limite inválido para o campo `nome` evitando erros de truncamento em banco de dados conforme HU-08.1. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. |
