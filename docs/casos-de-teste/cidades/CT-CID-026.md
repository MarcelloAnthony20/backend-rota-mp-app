# Caso de Teste - Cadastrar cidade sem perfil de Gestor - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-026 |
| **Nome do Caso de Teste** | Cadastrar cidade sem perfil de Gestor |
| **Funcionalidade Testada** | Segurança / Autorização RBAC no Cadastro |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Autorização / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que usuários autenticados sem o perfil de Gestor (ex.: perfil Operador) têm o acesso bloqueado ao tentar cadastrar uma nova cidade via payload `CriarCidadeRequest`, recebendo status HTTP 403 Forbidden. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com credenciais válidas vinculadas exclusivamente ao perfil `Operador` (sem permissão administrativa). |
| 2 | Serviço de cadastro de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Montar payload JSON (`CriarCidadeRequest`) contendo apenas os campos `nome` (`"Nova Cidade"`), `codigo` (`"5002407"`) e `ativo` (`true`), sem envio de ID no corpo. |
| 2 | Incluir no cabeçalho `Authorization` o token JWT do usuário com perfil `Operador`. |
| 3 | Enviar a requisição para o endpoint `POST /api/v1/cidades`. |
| 4 | Consultar o código de status HTTP retornado pelo gateway/aplicação. |
| 5 | Verificar na base de dados se algum registro foi inserido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Nova Cidade` |
| Código da Cidade | `5002407` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Nova Cidade", "codigo": "5002407", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `Bearer <token_operador_valido>` |
| Perfil do Usuário | `Operador` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo mecanismo de controle de acesso retornando status HTTP `403 Forbidden`. |
| 2 | Resposta estruturada conforme RFC 7807 indicando privilégios insuficientes para execução da operação. |
| 3 | Nenhum registro é gravado na base de dados. |

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
| Observações | Atendimento ao controle de perfis RBAC de HU-08.1: somente usuários com perfil de Gestor ou superior possuem permissão para inclusão de novas cidades no catálogo do sistema. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. |
