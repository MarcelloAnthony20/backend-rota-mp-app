# Caso de Teste - Adicionar cidade com nome no limite de 200 caracteres - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-005 |
| **Nome do Caso de Teste** | Adicionar cidade com nome no limite de 200 caracteres |
| **Funcionalidade Testada** | Cadastro de cidade |
| **Prioridade** | Média |
| **Tipo de Teste** | Valor Limite / Teste Positivo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a aceitação e persistência íntegra do cadastro de cidade quando o campo nome possui exatamente 200 caracteres (limite máximo permitido), confirmando que o payload `CriarCidadeRequest` não recebe ID e que o sistema gera o UUID na resposta. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Serviço de cadastro de cidades disponível e operacional. |
| 3 | Código `5008305` não cadastrado previamente na base de dados (distinto das seeds fixas). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de inclusão de cidade (`POST /api/v1/cidades`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON contendo apenas os campos `nome` (composto por exatamente 200 caracteres), `codigo` (`"5008305"`) e `ativo` (`true`), conforme o contrato `CriarCidadeRequest`. O envio é feito sem ID e o sistema gera o UUID na resposta. |
| 4 | Consultar o código de resposta HTTP e os dados retornados no payload. |
| 5 | Consultar a base de dados para confirmar a persistência dos 200 caracteres sem truncamento e com o UUID gerado. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Cidade Modelo de Testes Homologados para Validar Limite Superior Extremo com Exatamente Duzentos Caracteres Alfanuméricos Permitidos nas Regras de Validação Cadastral do Sistema Rota MP APP no Estado ` (200 caracteres) |
| Código da Cidade | `5008305` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Cidade Modelo...Estado ", "codigo": "5008305", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação aceita com sucesso retornando status HTTP `201 Created`. |
| 2 | Nome persistido integralmente na base de dados com todos os 200 caracteres sem cortes ou perdas. |
| 3 | Registro criado com status ativo (`ativo = true`) e resposta contendo o DTO `CidadeResponse` com ID gerado pelo sistema (formato UUID). |
| 4 | Cabeçalho `Location` informando a URI de acesso ao recurso recém-criado (`/api/v1/cidades/{id}`). |

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
| Observações | Teste de limite superior exato para o campo `nome` (VARCHAR 200) conforme DER e regras de validação de HU-08.1. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. |
