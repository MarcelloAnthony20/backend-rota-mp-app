# Caso de Teste - Alterar nome para valor acima de 200 caracteres - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-018 |
| **Nome do Caso de Teste** | Alterar nome para valor acima de 200 caracteres |
| **Funcionalidade Testada** | Alteração de cidade |
| **Prioridade** | Alta |
| **Tipo de Teste** | Valor Limite / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar a rejeição de alteração do nome da Seed Cidade A para um valor com 201 caracteres (acima do limite máximo de 200) no payload `AtualizarCidadeRequest`, preservando intactos os dados cadastrais prévios. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Usuário autenticado com perfil de Gestor ou Administrador. |
| 2 | Seed Cidade A cadastrada no banco de dados com ID `c1d00001-0000-0000-0000-000000000001`, Nome: `"Campo Grande"`, Código: `"5002704"`, `ativo = true`. |
| 3 | Serviço de alteração de cidades disponível e operacional. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Acessar o endpoint de alteração de cidades informando a Seed Cidade A (`PUT /api/v1/cidades/c1d00001-0000-0000-0000-000000000001`). |
| 2 | Informar o cabeçalho `Authorization: Bearer <token_gestor>`. |
| 3 | Enviar payload JSON (`AtualizarCidadeRequest`) contendo string de 201 caracteres no campo nome, código `5002704` e status ativo `true` (sem ID no corpo). |
| 4 | Consultar o código de status HTTP retornado e a mensagem de erro da resposta. |
| 5 | Consultar a base de dados para garantir que os dados prévios da Seed Cidade A permanecem inalterados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade A |
| ID da Cidade (Path Variable) | `c1d00001-0000-0000-0000-000000000001` |
| Nome da Cidade | `Cidade Modelo de Testes Homologados para Validar Limite Superior Extremo com Exatamente Duzentos Caracteres Alfanuméricos Permitidos nas Regras de Validação Cadastral do Sistema Rota MP APP no Estado X` (201 caracteres) |
| Código da Cidade | `5002704` |
| Status / Ativo | `Ativo (true)` |
| Payload da API (`AtualizarCidadeRequest`) | `{"nome": "Cidade Modelo...Estado X", "codigo": "5002704", "ativo": true}` (sem ID no corpo) |
| Método HTTP | `PUT` |
| Cabeçalho Authorization | `Bearer <token_gestor>` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada com status HTTP `400 Bad Request` ou `422 Unprocessable Entity`. |
| 2 | Mensagem de erro informativa no formato RFC 7807 (ProblemDetail) indicando violação do limite máximo de 200 caracteres para o nome. |
| 3 | O registro da Seed Cidade A permanece inalterado no banco com os dados prévios, código `5002704` e status `ativo = true`. |
| 4 | Nenhuma mutação parcial ou inconsistência de dados é gerada. |

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
| Observações | Validação de integridade: em caso de erro de validação na alteração, todos os dados anteriores da Seed Cidade A (`c1d00001-0000-0000-0000-000000000001`) são mantidos intactos conforme HU-08.3. |
