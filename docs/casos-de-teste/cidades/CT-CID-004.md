# Caso de Teste - Adicionar cidade com código acima do limite - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-004 |
| **Nome do Caso de Teste** | Adicionar cidade com código acima do limite |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Valor Limite / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a rejeição do cadastro quando o campo código possui 8 caracteres no payload `CriarCidadeRequest`, imediatamente acima do limite máximo permitido de 7 caracteres. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades disponível e operacional. |
| 3 | Base de dados acessível e conectada ao serviço. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON contendo apenas os campos `nome` (`"Três Lagoas"`), código com 8 caracteres (`"50024071"`) e status ativo (`true`), conforme `CriarCidadeRequest`. O envio é feito sem ID. |
| 4 | Consultar o código de status HTTP e o corpo da resposta retornada. |
| 5 | Consultar a base de dados para garantir que nenhum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Três Lagoas` |
| Código da Cidade | `50024071` (8 caracteres) |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Três Lagoas", "codigo": "50024071", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro informativa no formato RFC 7807 (ProblemDetail) indicando que o código excede o tamanho máximo de 7 caracteres. |
| 3 | Nenhum registro é gravado no banco de dados, mantendo a integridade estrutural. |
| 4 | Nenhuma exceção não tratada ou erro de banco de dados (ex.: DataTruncation) é exposto ao cliente. |

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
| Observações | Teste de valor limite inválido logo acima do máximo (8 caracteres para limite de 7 caracteres na coluna código) conforme DER e HU-08.1. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. |
