# Caso de Teste - Adicionar cidade com dados válidos - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-002 |
| **Nome do Caso de Teste** | Adicionar cidade com dados válidos |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Teste Positivo / Fluxo Feliz |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar o cadastro de uma nova cidade informando dados válidos com persistência íntegra, verificação de que o payload `CriarCidadeRequest` não recebe ID, e retorno HTTP 201 Created com UUID gerado pelo sistema. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades disponível e operacional. |
| 3 | Código `5002407` não cadastrado previamente na base de dados (distinto das seeds fixas). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON contendo apenas os campos `nome` (`"Bonito"`), `codigo` (`"5002407"`) e `ativo` (`true`), conforme o contrato de `CriarCidadeRequest`. O envio é feito sem ID e o sistema gera o UUID automaticamente na resposta. |
| 4 | Consultar o código de status HTTP retornado e os dados do payload de resposta. |
| 5 | Consultar a base de dados para confirmar a persistência dos valores cadastrados com o UUID gerado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Bonito` |
| Código da Cidade | `5002407` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Bonito", "codigo": "5002407", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `201 Created`. |
| 2 | Resposta estruturada (`CidadeResponse`) contendo o ID gerado automaticamente pelo sistema no formato UUID v4, nome `"Bonito"`, código `"5002407"` e `ativo = true`. |
| 3 | Registro da nova cidade persistido na base de dados com estado obrigatoriamente ativo (`ativo = true`). |
| 4 | Cabeçalho `Location` informando a URI de acesso ao recurso recém-criado com o UUID gerado (`/api/v1/cidades/{id}`). |

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
| Observações | Criação de nova entidade canônica de teste via API. Conforme arquitetura do backend, o ID não faz parte de `CriarCidadeRequest`, sendo gerado internamente pelo banco/sistema (UUID v4). As seeds fixas pré-cadastradas (`c1d00001` a `c1d00004`) são reservadas para cenários de alteração, inativação, consulta e reativação. |
