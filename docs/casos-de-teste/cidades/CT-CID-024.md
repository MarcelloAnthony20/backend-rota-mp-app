# Caso de Teste - Inativar cidade com token inválido ou expirado - Rota MP APP

| Campo | Valor |
|---------|---------|
| **ID do Caso de Teste** | CT-CID-024 |
| **Nome do Caso de Teste** | Inativar cidade com token inválido ou expirado |
| **Funcionalidade Testada** | Segurança / Validação de Token na Inativação |
| **Prioridade** | Alta |
| **Tipo de Teste** | Segurança / Token Inválido / Teste Negativo |

## Objetivo

| Campo | Descrição |
|---------|---------|
| **Objetivo** | Validar que a tentativa de inativar a Seed Cidade B utilizando um token de autenticação JWT expirado, malformado ou com assinatura inválida é rejeitada com status HTTP 401 Unauthorized. |

## Condições Pré-Teste

| Nº | Condição |
|---------|---------|
| 1 | Seed Cidade B cadastrada na base de dados (ID `c1d00002-0000-0000-0000-000000000002`, Nome: `"Dourados"`, Código: `"5003702"`). |
| 2 | Token JWT expirado ou adulterado disponível para o cenário de teste. |
| 3 | Filtro de validação de tokens JWT ativo no serviço. |

## Passos de Execução

| Passo | Ação |
|---------|---------|
| 1 | Montar requisição `DELETE /api/v1/cidades/c1d00002-0000-0000-0000-000000000002`. |
| 2 | Incluir no cabeçalho `Authorization` o valor `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.tokenInvalidoOuExpirado`. |
| 3 | Enviar a requisição para o servidor. |
| 4 | Consultar o código de status HTTP e a resposta de erro retornada. |
| 5 | Verificar que o registro da Seed Cidade B permaneceu inalterado na base de dados. |

## Dados de Entrada

| Campo | Valor |
|---------|---------|
| Entidade / Seed | Seed Cidade B |
| ID da Cidade | `c1d00002-0000-0000-0000-000000000002` |
| Nome da Cidade | `Dourados` |
| Código da Cidade | `5003702` |
| Status / Ativo | `Ativo (true)` |
| Método HTTP | `DELETE` |
| Cabeçalho Authorization | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.tokenInvalidoOuExpirado` |
| Perfil do Usuário | `Token inválido / expirado` |

## Resultado Esperado

| Nº | Resultado |
|---------|---------|
| 1 | Operação rejeitada retornando status HTTP `401 Unauthorized`. |
| 2 | Mensagem de erro informativa indicando token inválido, expirado ou assinatura não reconhecida (RFC 7807). |
| 3 | O registro da Seed Cidade B permanece inalterado na base de dados. |
| 4 | Nenhuma informação confidencial de infraestrutura é exposta. |

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
| Observações | Proteção de segurança contra reutilização de tokens JWT vencidos ou forjados conforme HU-08.4. Utiliza explicitamente a Seed Cidade B (`c1d00002-0000-0000-0000-000000000002`). |
