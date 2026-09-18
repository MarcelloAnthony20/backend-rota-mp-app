# Caso de Teste - Cadastrar cidade sem autenticação - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-027 |
| **Nome do Caso de Teste** | Cadastrar cidade sem autenticação |
| **Funcionalidade Testada** | Segurança / Autenticação no Cadastro |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Autenticação / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de cadastrar uma nova cidade sem fornecimento de token de autenticação no cabeçalho HTTP é rejeitada imediatamente com status HTTP 401 Unauthorized, confirmando a proteção do endpoint `POST /api/v1/cidades`. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Mecanismo de segurança JWT / Spring Security ativo e operacional. |
| 2 | Endpoint de cadastro de cidades disponível (`POST /api/v1/cidades`). |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Montar payload JSON (`CriarCidadeRequest`) contendo apenas os campos `nome` (`"Ponta Porã"`), `codigo` (`"5006606"`) e `ativo` (`true`), sem envio de ID no corpo. |
| 2 | Enviar requisição `POST /api/v1/cidades` omitindo totalmente o cabeçalho `Authorization`. |
| 3 | Consultar o código de status HTTP retornado pelo servidor. |
| 4 | Validar o corpo da resposta de erro retornada. |
| 5 | Verificar na base de dados se algum registro foi persistido. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| ID da Cidade | Gerado automaticamente pelo sistema (não enviado no payload de criação) |
| Nome da Cidade | `Ponta Porã` |
| Código da Cidade | `5006606` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`CriarCidadeRequest`) | `{"nome": "Ponta Porã", "codigo": "5006606", "ativo": true}` (apenas `nome`, `codigo` e `ativo`) |
| Método HTTP | `POST` |
| Cabeçalho Authorization | `""` (Ausente / não informado) |
| Perfil do Usuário | `Não autenticado (Anônimo)` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo filtro de segurança retornando status HTTP `401 Unauthorized`. |
| 2 | Resposta de erro estruturada conforme RFC 7807 indicando ausência de credenciais válidas. |
| 3 | Nenhum registro é gravado na base de dados (atomicidade garantida). |

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
| Observações | Validação mandatória de controle de acesso anônimo para prevenção de inclusões não autenticadas no catálogo conforme HU-08.1. O payload `CriarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. |
