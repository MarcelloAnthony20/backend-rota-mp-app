# Caso de Teste - Cadastrar cidade com código duplicado - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-025 |
| **Nome do Caso de Teste** | Cadastrar cidade com código duplicado |
| **Funcionalidade Testada** | Cadastro de cidade / Unicidade de código |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Unicidade |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que o sistema impede o cadastro de uma nova cidade cujo código informado já pertença a outra cidade cadastrada (Seed Cidade A: código `5002704`), retornando status HTTP 409 Conflict e confirmando que o payload `CriarCidadeRequest` é submetido sem campo ID. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade A previamente cadastrada no banco de dados com código `5002704` (ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, `ativo = true`). |
| 3 | Serviço de cadastro de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar payload JSON (`CriarCidadeRequest`) contendo apenas os campos `nome` (`"Nova Cidade"`), `codigo` idêntico ao da Seed Cidade A (`"5002704"`) e `ativo` (`true`). O envio é feito sem campo ID. |
| 3 | Enviar a requisição acompanhada do token de autorização de Gestor. |
| 4 | Consultar o código de status HTTP retornado e a estrutura da resposta. |
| 5 | Verificar na base de dados se a nova cidade foi persistida. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Nova Cidade` |
| Código da Cidade | `5002704` (duplicado da Seed Cidade A) |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Nova Cidade", "codigo": "5002704", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `409 Conflict`. |
| 2 | Mensagem de erro informativa indicando conflito por duplicidade do código da cidade (RFC 7807). |
| 3 | Nenhum novo registro é persistido na base de dados (atomicidade preservada). |
| 4 | Os dados da Seed Cidade A permanecem inalterados. |

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
| Observações | Atendimento ao critério de unicidade de HU-08.1: "retornar erro HTTP 409 Conflict caso o código informado já esteja cadastrado para outra cidade". O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. Referência: código `5002704` pertencente à Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`). |
