# Caso de Teste - Adicionar cidade diretamente com status inativo - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-007 |
| **Nome do Caso de Teste** | Adicionar cidade diretamente com status inativo |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Teste Positivo / Criação com Status Específico |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar o cadastro direto de uma nova cidade com status inicial inativo (`ativo = false`) na base de dados, garantindo que o payload `CriarCidadeRequest` não recebe ID e que o sistema gera o UUID na resposta. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades disponível e operacional. |
| 3 | Código `5001102` não cadastrado previamente na base de dados (distinto das seeds fixas). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON contendo apenas os campos `nome` (`"Aquidauana"`), `codigo` (`"5001102"`) e status inativo `ativo` (`false`), conforme o contrato de `CriarCidadeRequest`. O envio é feito sem ID e o sistema gera o UUID automaticamente na resposta. |
| 4 | Consultar o código de resposta HTTP retornado e os dados do payload de resposta. |
| 5 | Consultar a base de dados para confirmar a persistência com status inativo (`ativo = false`) e com o UUID gerado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Aquidauana` |
| Código da Cidade | `5001102` |
| Status / Ativo | `Inativo (false)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Aquidauana", "codigo": "5001102", "ativo": false}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `201 Created`. |
| 2 | Resposta estruturada (`CidadeResponse`) contendo o ID gerado pelo sistema (formato UUID v4), nome `"Aquidauana"`, código `"5001102"` e `ativo = false`. |
| 3 | Registro persistido no banco de dados com estado inativo (`ativo = false`). |
| 4 | Cabeçalho `Location` apontando para a URI do novo recurso criado com o UUID gerado (`/api/v1/cidades/{id}`). |

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
| Observações | Criação de cidade com estado inativo inicial conforme HU-08.1. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. A Seed Cidade D (`c1d00004-0000-0000-0000-000000000004`, Corumbá) já é inicializada como inativa pelo `CidadeDataInitializer` para utilização nos testes de reativação (CT-CID-010). |
