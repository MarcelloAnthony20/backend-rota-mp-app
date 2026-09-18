# Caso de Teste - Alterar cidade com código duplicado de outra cidade - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-032 |
| **Nome do Caso de Teste** | Alterar cidade com código duplicado de outra cidade |
| **Funcionalidade Testada** | Alteração de cidade / Unicidade de código |
| **Prioridade** | Crítica |
| **Tipo de Teste** | Regra de Negócio / Teste Negativo / Unicidade |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de alteração do código de uma cidade (Seed Cidade B) para um valor já pertencente a outra cidade cadastrada (código `5002704` da Seed Cidade A) via payload `AtualizarCidadeRequest` é rejeitada pelo sistema com status HTTP 409 Conflict. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou superior. |
| 2 | Seed Cidade A existente na base de dados com código `5002704` (ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, `ativo = true`). |
| 3 | Seed Cidade B existente na base de dados com ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"` e código original `5003702`. |
| 4 | Endpoint de alteração de cidades operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Montar requisição de alteração para a Seed Cidade B (`PUT /api/v1/cidades/c1d00002-0000-0000-0000-000000000002`). |
| 2 | Informar no payload de atualização (`AtualizarCidadeRequest`) apenas os campos `nome` (`"Dourados Alterada"`), o código duplicado `5002704` (pertencente à Seed Cidade A) e status `ativo` (`true`). O envio é feito sem ID no corpo da mensagem. |
| 3 | Enviar a requisição acompanhada do token de autorização de Gestor. |
| 4 | Consultar o código de status HTTP retornado e a estrutura da resposta. |
| 5 | Verificar no banco de dados se a Seed Cidade B manteve seus dados originais intactos. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade Alvo da Alteração | Seed Cidade B |
| ID da Cidade (Path Variable) | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados Alterada` |
| Código da Cidade | `5002704` (duplicado da Seed Cidade A `c1d00001-0000-0000-0000-000000000001`) |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`AtualizarCidadeRequest`) | `{"nome": "Dourados Alterada", "codigo": "5002704", "ativo": true}` (sem ID no corpo) |
| Método HTTP | `PUT` |
| Cabeçalho Authorization | `Bearer <token_gestor_valido>` |
| Perfil do Usuário | `Gestor` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada pelo sistema retornando status HTTP `409 Conflict`. |
| 2 | Mensagem de erro estruturada informa violação da restrição de unicidade do código (RFC 7807). |
| 3 | O registro da Seed Cidade B mantém seu código original `5003702` inalterado no banco de dados. |
| 4 | O registro da Seed Cidade A permanece intacto. |

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
| Observações | Atendimento ao critério de HU-08.3: "retornar erro HTTP 409 Conflict caso o novo código informado já esteja cadastrado para outra cidade". O payload `AtualizarCidadeRequest` contempla apenas `nome`, `codigo` e `ativo`. Utiliza explicitamente a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`) e o código da Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`). |
