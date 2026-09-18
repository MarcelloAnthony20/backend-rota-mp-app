# Caso de Teste - Adicionar cidade sem nome - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-001 |
| **Nome do Caso de Teste** | Adicionar cidade sem nome |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Negativo / Validação de Campo Obrigatório |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema rejeita o cadastro de cidade quando o campo nome é enviado vazio ou ausente no payload `CriarCidadeRequest`, sem realizar persistência parcial na base de dados. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades operacional e acessível via API REST. |
| 3 | Código `5002407` disponível para teste na base de dados (não conflitante com as seeds fixas). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidades (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho de autenticação `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON (`CriarCidadeRequest`) contendo apenas o campo nome vazio (`""`), código `5002407` e status ativo `true`, sem envio de ID no payload (o sistema gera o UUID na resposta apenas em caso de sucesso). |
| 4 | Consultar o código de status HTTP retornado e o corpo da resposta. |
| 5 | Consultar a base de dados para certificar que nenhum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `""` (Vazio) |
| Código da Cidade | `5002407` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "", "codigo": "5002407", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 (ProblemDetail) informando que o campo nome é obrigatório e não pode ser vazio. |
| 3 | Nenhum registro é persistido na base de dados (atomicidade e consistência garantidas). |
| 4 | Nenhuma exceção não tratada ou stack trace de banco de dados é exposto na resposta da API. |

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
| Observações | Validação de campo obrigatório antes da persistência no banco de dados conforme especificação de negócio HU-08.1. O payload `CriarCidadeRequest` contempla estritamente `nome`, `codigo` e `ativo`. |
