# Caso de Teste - Adicionar cidade com código no limite máximo permitido - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-003 |
| **Nome do Caso de Teste** | Adicionar cidade com código no limite máximo permitido |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Valor Limite / Teste Positivo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a aceitação e persistência íntegra do cadastro de uma nova cidade informando código com exatamente 7 caracteres (limite máximo permitido) e status ativo, garantindo que o payload `CriarCidadeRequest` não recebe ID e que o sistema gera o identificador UUID na resposta. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades disponível e operacional. |
| 3 | Código `5006606` (7 caracteres) não cadastrado previamente na base de dados (distinto das seeds fixas). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON contendo apenas os campos `nome` (`"Ponta Porã"`), `codigo` com exatamente 7 caracteres (`"5006606"`) e `ativo` (`true`), conforme `CriarCidadeRequest`. O envio é feito sem ID e o sistema gera o UUID automaticamente na resposta. |
| 4 | Consultar o código de resposta HTTP e os dados retornados no corpo da resposta. |
| 5 | Consultar a base de dados para confirmar que o registro foi persistido com sucesso e com o UUID gerado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Ponta Porã` |
| Código da Cidade | `5006606` (7 caracteres) |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Ponta Porã", "codigo": "5006606", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `201 Created`. |
| 2 | Código persistido com exatamente 7 caracteres (`5006606`) sem truncamento ou arredondamento. |
| 3 | Registro persistido na base com status ativo (`ativo = true`) e resposta contendo o DTO `CidadeResponse` com ID no formato UUID gerado pelo sistema. |
| 4 | Cabeçalho `Location` apontando para o endpoint da cidade recém-criada (`/api/v1/cidades/{id}`). |

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
| Observações | Cadastro no limite máximo de 7 caracteres para a coluna código conforme DER e HU-08.1. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. A entidade canônica Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`) é mantida separada e imutável no banco para os cenários de inativação e integridade referencial. |
